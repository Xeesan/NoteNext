package com.suvojeet.notenext.ui.notes

sealed class NotesUiEvent {
    data class SendNotes(val title: String, val content: String) : NotesUiEvent()
    /** A collaborative share link was created and is ready to share / open. */
    data class ShareLinkReady(val url: String, val shareId: String, val title: String) : NotesUiEvent()
    data class ShowSnackbar(
        val message: String,
        val actionLabel: String? = null,
        val onAction: (() -> Unit)? = null
    ) : NotesUiEvent()
    object LinkPreviewRemoved : NotesUiEvent()
    data class ProjectCreated(val projectName: String) : NotesUiEvent()
    data class NavigateToNoteByTitle(val title: String) : NotesUiEvent()
    data class ScrollToSearchResult(val index: Int) : NotesUiEvent()
}