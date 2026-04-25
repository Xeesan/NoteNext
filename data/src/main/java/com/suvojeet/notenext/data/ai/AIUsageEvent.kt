package com.suvojeet.notenext.data.ai

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * A single AI-feature invocation, recorded LOCALLY for the user's own dashboard.
 *
 * Privacy notes:
 * - The note content is NEVER stored here. We only record metadata:
 *   which feature was invoked, which provider serviced it, when, how long
 *   it took, whether it succeeded, and (for suggestion features) whether
 *   the user accepted the suggestion.
 * - Tracking can be disabled in AI Settings; when off, no rows are written.
 * - Users can clear all rows from the dashboard.
 */
@Immutable
@Entity(
    tableName = "ai_usage_events",
    indices = [
        Index(value = ["timestamp"]),
        Index(value = ["featureId"]),
        Index(value = ["provider"])
    ]
)
data class AIUsageEvent(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val featureId: String,
    val provider: String,
    val timestamp: Long,
    val success: Boolean,
    val durationMs: Long,
    /** null = N/A (not a suggestion feature), true = user accepted, false = user dismissed */
    val accepted: Boolean? = null
)
