package com.suvojeet.notenext.data.ai

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Single entry point for every AI feature in the app.
 *
 * Responsibilities:
 *  - Route a request to the user's chosen provider (Groq / OpenAI / Anthropic / Gemini).
 *  - Honor the AIFeatureGate before doing anything network-bound.
 *  - Record an AIUsageEvent into the local dashboard regardless of outcome.
 *
 * If the master switch or a per-feature toggle is off, this returns
 * `AIResult.AuthError("disabled")` immediately and records nothing — the UI
 * should never even reach this point because feature buttons are hidden when
 * disabled, but we double-check here as a safety net.
 */
@Singleton
class AIProviderManager @Inject constructor(
    private val groqProvider: GroqProvider,
    private val openAIProvider: OpenAIProvider,
    private val anthropicProvider: AnthropicProvider,
    private val geminiProvider: GeminiProvider,
    private val settingsRepository: com.suvojeet.notenext.data.repository.SettingsRepository,
    private val featureGate: AIFeatureGate,
    private val usageRepository: AIUsageRepository
) {
    private val mutex = Mutex()
    private val _activeProvider = MutableStateFlow<AIProvider>(AIProvider.GROQ)
    val activeProvider: Flow<AIProvider> = _activeProvider

    private val providers = mutableMapOf<AIProvider, AIProviderService>()

    init {
        providers[AIProvider.GROQ] = groqProvider
        providers[AIProvider.OPENAI] = openAIProvider
        providers[AIProvider.ANTHROPIC] = anthropicProvider
        providers[AIProvider.GEMINI] = geminiProvider
    }

    suspend fun setActiveProvider(provider: AIProvider) {
        mutex.withLock {
            settingsRepository.savePreferredAIProvider(provider.name)
            _activeProvider.value = provider
        }
    }

    suspend fun getActiveProvider(): AIProvider {
        val providerName = settingsRepository.preferredAIProvider.first()
        return try {
            AIProvider.valueOf(providerName)
        } catch (e: Exception) {
            AIProvider.GROQ
        }
    }

    suspend fun getProviderService(provider: AIProvider): AIProviderService? = providers[provider]

    suspend fun isProviderAvailable(provider: AIProvider): Boolean =
        providers[provider]?.isProviderAvailable() == true

    suspend fun getAllAvailableProviders(): List<AIProvider> =
        providers.keys.filter { providers[it]?.isProviderAvailable() == true }

    // ─── Gated, tracked invocations ──────────────────────────────────────

    suspend fun summarizeNote(content: String): AIResult<String> =
        invoke(AIFeature.SUMMARIZE) { it.summarizeNote(content) }

    suspend fun generateChecklist(topic: String): AIResult<List<String>> =
        invoke(AIFeature.CHECKLIST) { it.generateChecklist(topic) }

    suspend fun generateTodos(input: String): AIResult<List<Pair<String, String>>> =
        invoke(AIFeature.TODOS) { it.generateTodos(input) }

    suspend fun fixGrammar(text: String): AIResult<String> =
        invoke(AIFeature.GRAMMAR) { it.fixGrammar(text) }

    suspend fun generateCustomPrompt(systemPrompt: String, userPrompt: String): AIResult<String> =
        invoke(AIFeature.CUSTOM_PROMPT) { it.generateCustomPrompt(systemPrompt, userPrompt) }

    suspend fun suggestLabels(content: String, existingLabels: List<String>): AIResult<List<String>> =
        invoke(AIFeature.AUTO_TAG) { it.suggestLabels(content, existingLabels) }

    suspend fun extractReminders(content: String): AIResult<List<ExtractedReminder>> =
        invoke(AIFeature.SMART_REMINDER) { it.extractReminders(content, System.currentTimeMillis()) }

    suspend fun rewriteWithTone(text: String, tone: ToneOption): AIResult<String> =
        invoke(AIFeature.TONE_REWRITE) { it.rewriteWithTone(text, tone) }

    suspend fun extractKeywords(content: String): AIResult<List<String>> =
        invoke(AIFeature.LINKED_NOTES) { it.extractKeywords(content) }

    /**
     * Mark a user's accept/dismiss action on the most recent suggestion-style
     * invocation of a feature so the dashboard can compute acceptance rate.
     */
    suspend fun recordSuggestionAccepted(feature: AIFeature, accepted: Boolean) {
        usageRepository.record(
            feature = feature,
            provider = getActiveProvider(),
            success = true,
            durationMs = 0,
            accepted = accepted
        )
    }

    private suspend fun <T> invoke(
        feature: AIFeature,
        block: suspend (AIProviderService) -> AIResult<T>
    ): AIResult<T> {
        if (!featureGate.isEnabled(feature)) {
            return AIResult.AuthError("Feature '${feature.displayName}' is disabled in AI Settings.")
        }
        val provider = getActiveProvider()
        val service = providers[provider]
            ?: return AIResult.ProviderError("Provider not available: $provider")

        val start = System.currentTimeMillis()
        val result = block(service)
        val duration = System.currentTimeMillis() - start

        usageRepository.record(
            feature = feature,
            provider = provider,
            success = result is AIResult.Success,
            durationMs = duration,
            accepted = if (feature.isSuggestionFeature) null else null
        )
        return result
    }
}
