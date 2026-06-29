package com.suvojeet.notenext.data.share

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Request body for creating/updating a shared note. */
@Serializable
data class ShareNoteRequest(
    val title: String,
    val content: String,
    val collaborators: List<String> = emptyList(),
    val sharedBy: String? = null
)

/**
 * A shared note as returned by the backend. Unknown keys (_id, __v, etc.) are
 * ignored by the JSON config, so only the fields we care about are listed.
 */
@Serializable
data class SharedNoteDto(
    val shareId: String? = null,
    val title: String = "",
    val content: String = "",
    val sharedBy: String? = null,
    val version: Int = 1,
    val collaborators: List<String> = emptyList(),
    val createdAt: String? = null,
    val updatedAt: String? = null
)

/** Response from POST /api/notes/share. */
@Serializable
data class ShareNoteResponse(
    val message: String? = null,
    val shareId: String,
    val shareUrl: String? = null,
    /**
     * Secret delete-token, returned exactly once at share time. Must be stored
     * on-device and presented to delete (unshare) the note later. Null when the
     * backend predates the delete feature.
     */
    val deleteToken: String? = null,
    val note: SharedNoteDto? = null
)

/** Result of creating a share link. */
data class ShareResult(
    val shareId: String,
    val url: String,
    /** Secret token required to later unshare this note (creator-only proof). */
    val deleteToken: String? = null
)

/**
 * An incoming collaborative edit pushed over Socket.IO. Either field may be
 * null when only the other one changed.
 */
data class NoteDelta(
    val title: String?,
    val content: String?
)
