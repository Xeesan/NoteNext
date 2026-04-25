package com.suvojeet.notenext.data.ai

import com.suvojeet.notenext.data.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Local-only repository for AI usage telemetry. Nothing in this class ever
 * leaves the device — it powers the user-facing usage dashboard.
 *
 * Honors the user's `aiUsageTrackingEnabled` preference: when disabled,
 * `record()` becomes a no-op.
 */
@Singleton
class AIUsageRepository @Inject constructor(
    private val dao: AIUsageDao,
    private val settingsRepository: SettingsRepository
) {

    suspend fun record(
        feature: AIFeature,
        provider: AIProvider,
        success: Boolean,
        durationMs: Long,
        accepted: Boolean? = null
    ) {
        if (!settingsRepository.aiUsageTrackingEnabled.first()) return
        dao.insert(
            AIUsageEvent(
                featureId = feature.id,
                provider = provider.name,
                timestamp = System.currentTimeMillis(),
                success = success,
                durationMs = durationMs,
                accepted = accepted
            )
        )
    }

    fun observeTotalCount(): Flow<Int> = dao.observeTotalCount()
    fun observeSuccessCount(): Flow<Int> = dao.observeSuccessCount()
    fun observeAcceptedCount(): Flow<Int> = dao.observeAcceptedCount()
    fun observeSuggestionTotal(): Flow<Int> = dao.observeSuggestionTotal()
    fun observePerFeature(): Flow<List<FeatureUsageRow>> = dao.observePerFeatureCounts()
    fun observePerProvider(): Flow<List<ProviderUsageRow>> = dao.observePerProviderCounts()
    fun observeFeatureStats(): Flow<List<FeatureStatsRow>> = dao.observeFeatureStats()
    fun observeSince(sinceMs: Long): Flow<List<AIUsageEvent>> = dao.observeSince(sinceMs)
    fun observeRecent(limit: Int = 50): Flow<List<AIUsageEvent>> = dao.observeRecent(limit)

    suspend fun clearAll() = dao.deleteAll()
    suspend fun clearOlderThan(olderThanMs: Long) = dao.deleteOlderThan(olderThanMs)
}
