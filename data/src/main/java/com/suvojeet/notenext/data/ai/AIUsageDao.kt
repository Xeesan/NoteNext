package com.suvojeet.notenext.data.ai

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AIUsageDao {

    @Insert
    suspend fun insert(event: AIUsageEvent): Long

    @Query("SELECT COUNT(*) FROM ai_usage_events")
    fun observeTotalCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM ai_usage_events WHERE success = 1")
    fun observeSuccessCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM ai_usage_events WHERE accepted = 1")
    fun observeAcceptedCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM ai_usage_events WHERE accepted IS NOT NULL")
    fun observeSuggestionTotal(): Flow<Int>

    @Query("SELECT featureId, COUNT(*) AS total FROM ai_usage_events GROUP BY featureId")
    fun observePerFeatureCounts(): Flow<List<FeatureUsageRow>>

    @Query("SELECT provider, COUNT(*) AS total FROM ai_usage_events GROUP BY provider")
    fun observePerProviderCounts(): Flow<List<ProviderUsageRow>>

    @Query("""
        SELECT featureId,
               COUNT(*) AS total,
               SUM(CASE WHEN success = 1 THEN 1 ELSE 0 END) AS successes,
               SUM(CASE WHEN accepted = 1 THEN 1 ELSE 0 END) AS accepted,
               SUM(CASE WHEN accepted IS NOT NULL THEN 1 ELSE 0 END) AS suggestions,
               AVG(durationMs) AS avgDurationMs
        FROM ai_usage_events
        GROUP BY featureId
    """)
    fun observeFeatureStats(): Flow<List<FeatureStatsRow>>

    @Query("SELECT * FROM ai_usage_events WHERE timestamp >= :sinceMs ORDER BY timestamp ASC")
    fun observeSince(sinceMs: Long): Flow<List<AIUsageEvent>>

    @Query("SELECT * FROM ai_usage_events ORDER BY timestamp DESC LIMIT :limit")
    fun observeRecent(limit: Int): Flow<List<AIUsageEvent>>

    @Query("DELETE FROM ai_usage_events")
    suspend fun deleteAll()

    @Query("DELETE FROM ai_usage_events WHERE timestamp < :olderThanMs")
    suspend fun deleteOlderThan(olderThanMs: Long)
}

data class FeatureUsageRow(val featureId: String, val total: Int)
data class ProviderUsageRow(val provider: String, val total: Int)
data class FeatureStatsRow(
    val featureId: String,
    val total: Int,
    val successes: Int,
    val accepted: Int,
    val suggestions: Int,
    val avgDurationMs: Double
)
