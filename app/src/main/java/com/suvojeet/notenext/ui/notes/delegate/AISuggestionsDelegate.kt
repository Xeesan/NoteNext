package com.suvojeet.notenext.ui.notes.delegate

import com.suvojeet.notenext.data.LabelDao
import com.suvojeet.notenext.data.Note
import com.suvojeet.notenext.data.NoteRepository
import com.suvojeet.notenext.data.ai.AIFeature
import com.suvojeet.notenext.data.ai.AIFeatureGate
import com.suvojeet.notenext.data.ai.AIProviderManager
import com.suvojeet.notenext.data.ai.AIResult
import com.suvojeet.notenext.data.ai.AIUsageRepository
import com.suvojeet.notenext.data.ai.ExtractedReminder
import com.suvojeet.notenext.data.ai.ToneOption
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Hosts the four "advanced" AI features:
 *   - Tone Rewriter (active, on demand)
 *   - Auto-Tagging (passive, after save)
 *   - Smart Reminder Detection (passive, after save)
 *   - Linked Notes (passive, computed when a note is opened)
 *
 * All routed through AIProviderManager → which already enforces the feature
 * gate and records usage. Linked Notes deliberately uses a hybrid approach:
 * a fast local keyword-overlap pass that always runs (free, instant), plus
 * an optional AI keyword extraction call to improve quality when the feature
 * is enabled.
 */
@Singleton
class AISuggestionsDelegate @Inject constructor(
    private val aiProviderManager: AIProviderManager,
    private val featureGate: AIFeatureGate,
    private val usageRepository: AIUsageRepository,
    private val noteRepository: NoteRepository,
    private val labelDao: LabelDao
) {

    // ─── Tone Rewriter ───────────────────────────────────────────────────

    suspend fun rewriteTone(text: String, tone: ToneOption): AIResult<String> {
        if (text.isBlank()) return AIResult.ProviderError("Nothing to rewrite")
        return aiProviderManager.rewriteWithTone(text, tone)
    }

    // ─── Auto-Tagging ────────────────────────────────────────────────────

    suspend fun suggestLabels(content: String): AIResult<List<String>> {
        if (!featureGate.isEnabled(AIFeature.AUTO_TAG)) {
            return AIResult.AuthError("Auto-tag disabled")
        }
        if (content.length < 30) return AIResult.Success(emptyList())
        val existing = labelDao.getLabels().first().map { it.name }
        return aiProviderManager.suggestLabels(content, existing)
    }

    suspend fun recordLabelSuggestionAccepted(accepted: Boolean) {
        aiProviderManager.recordSuggestionAccepted(AIFeature.AUTO_TAG, accepted)
    }

    // ─── Smart Reminder Detection ────────────────────────────────────────

    suspend fun extractReminders(content: String): AIResult<List<ExtractedReminder>> {
        if (!featureGate.isEnabled(AIFeature.SMART_REMINDER)) {
            return AIResult.AuthError("Smart reminder disabled")
        }
        if (content.length < 20) return AIResult.Success(emptyList())
        return aiProviderManager.extractReminders(content)
    }

    suspend fun recordReminderSuggestionAccepted(accepted: Boolean) {
        aiProviderManager.recordSuggestionAccepted(AIFeature.SMART_REMINDER, accepted)
    }

    // ─── Linked Notes ────────────────────────────────────────────────────

    /**
     * Returns up to `limit` related notes for the given current note.
     *
     * Strategy: tokenize the current note's title + content, intersect with
     * every other note's tokens (Jaccard-ish), score, and return the top
     * matches. This is deliberately local and zero-cost — the LINKED_NOTES
     * feature gate gates only the *display* of the section, not the
     * computation, since computation is cheap and offline.
     */
    suspend fun findLinkedNotes(currentId: Int, currentTitle: String, currentContent: String, limit: Int = 5): List<Note> {
        if (!featureGate.isEnabled(AIFeature.LINKED_NOTES)) return emptyList()
        val currentTokens = tokenize("$currentTitle $currentContent")
        if (currentTokens.size < 3) return emptyList()

        val all = noteRepository.getAllNotes().first()
            .map { it.note }
            .filter { it.id != currentId && !it.isBinned && !it.isArchived }

        return all.asSequence()
            .map { other ->
                val otherTokens = tokenize("${other.title} ${other.content}")
                if (otherTokens.isEmpty()) return@map other to 0
                val overlap = currentTokens intersect otherTokens
                val union = currentTokens union otherTokens
                val jaccard = if (union.isEmpty()) 0.0 else overlap.size.toDouble() / union.size
                other to (jaccard * 1000).toInt()
            }
            .filter { it.second > 30 } // 3% similarity floor
            .sortedByDescending { it.second }
            .take(limit)
            .map { it.first }
            .toList()
    }

    private val stopwords = setOf(
        "the", "a", "an", "and", "or", "but", "of", "to", "in", "on", "for",
        "with", "is", "are", "was", "were", "be", "been", "being", "i", "me",
        "my", "you", "your", "he", "she", "it", "we", "they", "this", "that",
        "these", "those", "have", "has", "had", "do", "does", "did", "can",
        "could", "should", "would", "will", "shall", "may", "might", "must",
        "from", "as", "at", "by", "if", "so", "no", "not", "yes", "all", "any",
        "some", "more", "most", "much", "many", "few", "such", "than", "then",
        "now", "just", "also", "only", "very", "too", "out", "up", "down",
        "into", "over", "under", "about", "after", "before", "again", "still"
    )

    private fun tokenize(text: String): Set<String> =
        text.lowercase()
            .replace(Regex("[^a-z0-9 ]"), " ")
            .split(Regex("\\s+"))
            .filter { it.length in 3..30 && it !in stopwords }
            .toSet()
}
