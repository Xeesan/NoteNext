package com.suvojeet.notenext.data.share

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Uploads notes to the sharing backend and fetches shared notes back. Network
 * calls are wrapped in [Result] so callers can surface friendly errors without
 * try/catch noise.
 */
@Singleton
class ShareRepository @Inject constructor(
    private val api: NoteNextApiService
) {
    /** Publishes a note and returns its share id + public URL + delete-token. */
    suspend fun shareNote(title: String, content: String): Result<ShareResult> = runCatching {
        val response = api.shareNote(ShareNoteRequest(title = title, content = content))
        val id = response.shareId
        ShareResult(
            shareId = id,
            url = response.shareUrl ?: ShareConstants.shareUrl(id),
            deleteToken = response.deleteToken
        )
    }

    /**
     * Deletes (unshares) a note from the backend. Requires the secret delete-token
     * issued at share time; only the creator holds it.
     */
    suspend fun deleteNote(shareId: String, deleteToken: String): Result<Unit> = runCatching {
        api.deleteNote(shareId, deleteToken)
        Unit
    }

    /** Fetches the current state of a shared note. */
    suspend fun getNote(shareId: String): Result<SharedNoteDto> = runCatching {
        api.getNote(shareId)
    }

    /** Persists the latest title/content for a shared note (best-effort snapshot). */
    suspend fun pushUpdate(shareId: String, title: String, content: String): Result<Unit> = runCatching {
        api.updateNote(shareId, ShareNoteRequest(title = title, content = content))
        Unit
    }
}
