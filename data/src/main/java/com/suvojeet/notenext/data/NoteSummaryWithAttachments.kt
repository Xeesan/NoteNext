package com.suvojeet.notenext.data

import androidx.compose.runtime.Immutable
import androidx.room.Embedded
import androidx.room.Relation
import kotlinx.collections.immutable.ImmutableList
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class NoteSummaryWithAttachments(
    @Embedded val note: NoteSummary,
    @Relation(
        parentColumn = "id",
        entityColumn = "noteId"
    )
    val attachments: ImmutableList<Attachment>,
    @Relation(
        parentColumn = "id",
        entityColumn = "noteId"
    )
    val checklistItems: ImmutableList<ChecklistItem>
)
