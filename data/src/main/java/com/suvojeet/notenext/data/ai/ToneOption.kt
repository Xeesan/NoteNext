package com.suvojeet.notenext.data.ai

enum class ToneOption(
    val displayName: String,
    val emoji: String,
    val instruction: String
) {
    PROFESSIONAL("Professional", "💼", "formal, polished, business-appropriate"),
    CASUAL("Casual", "😎", "relaxed, conversational, like talking to a friend"),
    CONCISE("Concise", "✂️", "as short and direct as possible, removing all fluff"),
    FORMAL("Formal", "🎩", "academic, structured, sophisticated vocabulary"),
    FRIENDLY("Friendly", "🤗", "warm, approachable, kind-hearted"),
    BULLETS("Bullet Points", "•", "rewritten as a clear bullet point list");
}

data class ExtractedReminder(
    val text: String,
    val timestampMs: Long,
    val confidence: Float = 1f
)

data class LabelSuggestion(
    val label: String,
    val isExisting: Boolean
)
