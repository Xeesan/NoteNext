package com.suvojeet.notenext.data.ai

/**
 * Supported AI providers
 */
enum class AIProvider {
    GROQ,
    OPENAI,
    ANTHROPIC,
    GEMINI,
    OLLAMA,
    CUSTOM
}

/**
 * Abstract interface for all AI providers.
 *
 * The four "advanced" methods (suggestLabels, extractReminders, rewriteWithTone,
 * extractKeywords) have default implementations built on top of `generateCustomPrompt`.
 * That keeps every provider on parity without forcing each one to re-implement
 * prompt engineering — a provider can override any default if it has a smarter way.
 */
interface AIProviderService {
    suspend fun summarizeNote(content: String): AIResult<String>
    suspend fun generateChecklist(topic: String): AIResult<List<String>>
    suspend fun generateTodos(input: String): AIResult<List<Pair<String, String>>>
    suspend fun fixGrammar(text: String): AIResult<String>
    suspend fun generateCustomPrompt(systemPrompt: String, userPrompt: String): AIResult<String>
    suspend fun isProviderAvailable(): Boolean
    suspend fun getProviderName(): String
    suspend fun getAvailableModels(): List<String>

    // ─── Advanced features (default impls — providers may override) ─────

    /**
     * Suggest 1–3 short labels for a note. Existing labels are passed in
     * so the model can reuse them when sensible (avoids label-list explosion).
     */
    suspend fun suggestLabels(content: String, existingLabels: List<String>): AIResult<List<String>> {
        val existingHint = if (existingLabels.isEmpty()) {
            "(no existing labels yet)"
        } else {
            "Existing labels the user already uses: ${existingLabels.joinToString(", ")}. Prefer reusing one of these when it fits."
        }
        val system = """
            You suggest 1 to 3 short, single-word or two-word labels for the user's note.
            $existingHint
            Reply with ONLY a comma-separated list of labels, lowercase, no quotes, no extra text.
            Example reply: work, meeting, q2-planning
        """.trimIndent()
        return when (val result = generateCustomPrompt(system, content.take(2000))) {
            is AIResult.Success -> AIResult.Success(parseLabelList(result.data, existingLabels))
            is AIResult.RateLimited -> result
            is AIResult.AuthError -> result
            is AIResult.NetworkError -> result
            is AIResult.ProviderError -> result
            is AIResult.AllProvidersFailed -> result
        }
    }

    /**
     * Find date/time commitments in a note and return them as structured reminders.
     * Returns an empty list if nothing actionable was detected.
     */
    suspend fun extractReminders(content: String, nowMs: Long): AIResult<List<ExtractedReminder>> {
        val nowIso = java.time.Instant.ofEpochMilli(nowMs).toString()
        val system = """
            You extract reminders from notes. The current time (UTC) is $nowIso.
            For each future commitment with a date or time, output one line in the form:
            ISO_DATETIME | short label
            Use ISO-8601 with timezone offset (e.g. 2026-04-30T15:00:00Z).
            Output nothing if no future reminders are found. No preamble, no JSON.
        """.trimIndent()
        return when (val result = generateCustomPrompt(system, content.take(2000))) {
            is AIResult.Success -> AIResult.Success(parseReminderList(result.data))
            is AIResult.RateLimited -> result
            is AIResult.AuthError -> result
            is AIResult.NetworkError -> result
            is AIResult.ProviderError -> result
            is AIResult.AllProvidersFailed -> result
        }
    }

    /**
     * Rewrite the given text in the chosen tone. The original meaning must be
     * preserved; only style changes.
     */
    suspend fun rewriteWithTone(text: String, tone: ToneOption): AIResult<String> {
        val system = """
            You are a writing assistant. Rewrite the user's text in the following tone:
            ${tone.displayName} (${tone.instruction}).
            Preserve the original meaning exactly. Do not add new facts.
            Reply with ONLY the rewritten text — no preamble, no quotes, no explanation.
        """.trimIndent()
        return generateCustomPrompt(system, text)
    }

    /**
     * Extract 3–8 topical keywords from a note for the linked-notes feature.
     */
    suspend fun extractKeywords(content: String): AIResult<List<String>> {
        val system = """
            Extract 3 to 8 short topical keywords that capture what this note is about.
            Reply with ONLY a comma-separated list, lowercase, no quotes, no extra text.
        """.trimIndent()
        return when (val result = generateCustomPrompt(system, content.take(2000))) {
            is AIResult.Success -> AIResult.Success(
                result.data.split(",")
                    .map { it.trim().trim('"', '\'', '.', '·').lowercase() }
                    .filter { it.isNotBlank() && it.length in 2..40 }
                    .take(8)
            )
            is AIResult.RateLimited -> result
            is AIResult.AuthError -> result
            is AIResult.NetworkError -> result
            is AIResult.ProviderError -> result
            is AIResult.AllProvidersFailed -> result
        }
    }
}

private fun parseLabelList(raw: String, existing: List<String>): List<String> {
    val cleaned = raw
        .replace("```", "")
        .lines()
        .joinToString(",")
        .replace("\n", ",")
    return cleaned.split(",")
        .map { it.trim().trim('"', '\'', '.', '·', '-', '*').lowercase() }
        .filter { it.isNotBlank() && it.length in 2..30 && !it.contains(" ").let { hasSpace -> hasSpace && it.length > 20 } }
        .distinct()
        .take(3)
}

private fun parseReminderList(raw: String): List<ExtractedReminder> {
    return raw.lines()
        .mapNotNull { line ->
            val parts = line.split("|", limit = 2)
            if (parts.size != 2) return@mapNotNull null
            val isoStr = parts[0].trim()
            val label = parts[1].trim()
            if (label.isBlank()) return@mapNotNull null
            try {
                val instant = java.time.OffsetDateTime.parse(isoStr).toInstant()
                ExtractedReminder(text = label, timestampMs = instant.toEpochMilli())
            } catch (_: Exception) {
                try {
                    val instant = java.time.Instant.parse(isoStr)
                    ExtractedReminder(text = label, timestampMs = instant.toEpochMilli())
                } catch (_: Exception) {
                    null
                }
            }
        }
        .filter { it.timestampMs > System.currentTimeMillis() }
        .take(5)
}

/**
 * Unified result type for all AI operations
 */
sealed class AIResult<out T> {
    data class Success<T>(val data: T) : AIResult<T>()
    data class RateLimited(val retryAfterSeconds: Int) : AIResult<Nothing>()
    data class AuthError(val message: String) : AIResult<Nothing>()
    data class NetworkError(val message: String) : AIResult<Nothing>()
    data class ProviderError(val message: String) : AIResult<Nothing>()
    object AllProvidersFailed : AIResult<Nothing>()
}

inline fun <T> AIResult<T>.onSuccess(action: (T) -> Unit): AIResult<T> {
    if (this is AIResult.Success) action(data)
    return this
}

inline fun <T> AIResult<T>.onFailure(action: (AIResult<Nothing>) -> Unit): AIResult<T> {
    if (this !is AIResult.Success) action(this as AIResult<Nothing>)
    return this
}

/**
 * Configuration for an AI provider
 */
data class AIProviderConfig(
    val provider: AIProvider,
    val apiKey: String = "",
    val baseUrl: String = "",
    val models: List<String> = emptyList(),
    val timeout: Int = 30,
    val isEnabled: Boolean = true
)
