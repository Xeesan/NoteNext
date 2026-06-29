package com.suvojeet.notenext.ui.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suvojeet.notenext.data.Note
import com.suvojeet.notenext.data.NoteRepository
import com.suvojeet.notenext.data.share.CollabSession
import com.suvojeet.notenext.data.share.CollaborationManager
import com.suvojeet.notenext.data.share.NoteDelta
import com.suvojeet.notenext.data.share.ShareRepository
import com.suvojeet.notenext.util.HtmlConverter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SharedNoteUiState(
    val loading: Boolean = true,
    val error: String? = null,
    val title: String = "",
    val content: String = "",          // plain text shown in the editor
    val sharedBy: String = "NoteNext user",
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val connected: Boolean = false,
    val savedLocally: Boolean = false
)

sealed interface SharedNoteEvent {
    data class Toast(val message: String) : SharedNoteEvent
    object SavedCopy : SharedNoteEvent
}

@HiltViewModel
class SharedNoteViewModel @Inject constructor(
    private val shareRepository: ShareRepository,
    private val collaborationManager: CollaborationManager,
    private val noteRepository: NoteRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SharedNoteUiState())
    val state = _state.asStateFlow()

    private val _events = MutableSharedFlow<SharedNoteEvent>(extraBufferCapacity = 8)
    val events = _events.asSharedFlow()

    private var shareId: String? = null
    private var session: CollabSession? = null
    private var started = false
    private var syncJob: Job? = null

    // Echo guard: ignore an incoming update that simply mirrors what we last sent.
    private var lastSentContentHtml: String? = null
    private var lastSentTitle: String? = null

    fun start(id: String) {
        if (started) return
        started = true
        shareId = id
        load(id)
    }

    private fun load(id: String) {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            shareRepository.getNote(id)
                .onSuccess { dto ->
                    val plain = if (dto.content.isNotBlank()) HtmlConverter.htmlToPlainText(dto.content) else ""
                    _state.update {
                        it.copy(
                            loading = false,
                            error = null,
                            title = dto.title,
                            content = plain,
                            sharedBy = dto.sharedBy?.takeIf { s -> s.isNotBlank() } ?: "NoteNext user",
                            createdAt = dto.createdAt,
                            updatedAt = dto.updatedAt
                        )
                    }
                    connectCollaboration(id)
                }
                .onFailure {
                    _state.update {
                        it.copy(loading = false, error = "Couldn't load this note. Check your connection and try again.")
                    }
                }
        }
    }

    private fun connectCollaboration(id: String) {
        val s = collaborationManager.open(id)
        session = s
        viewModelScope.launch { s.connected.collect { c -> _state.update { st -> st.copy(connected = c) } } }
        viewModelScope.launch { s.incoming.collect { delta -> applyRemote(delta) } }
        s.connect()
    }

    private suspend fun applyRemote(delta: NoteDelta) {
        // Skip our own echoes.
        if (delta.content != null && delta.content == lastSentContentHtml) return
        val newContent = delta.content?.let { HtmlConverter.htmlToPlainText(it) }
        _state.update { st ->
            st.copy(
                title = delta.title ?: st.title,
                content = newContent ?: st.content
            )
        }
    }

    fun onTitleChange(value: String) {
        _state.update { it.copy(title = value) }
        scheduleSync()
    }

    fun onContentChange(value: String) {
        _state.update { it.copy(content = value) }
        scheduleSync()
    }

    private fun scheduleSync() {
        val id = shareId ?: return
        syncJob?.cancel()
        syncJob = viewModelScope.launch {
            kotlinx.coroutines.delay(400)
            val snapshot = _state.value
            val html = plainToHtml(snapshot.content)
            lastSentTitle = snapshot.title
            lastSentContentHtml = html
            val s = session
            if (s != null && _state.value.connected) {
                s.sendEdit(snapshot.title, html)
            } else {
                // No live socket — persist directly so the change isn't lost.
                shareRepository.pushUpdate(id, snapshot.title, html)
            }
        }
    }

    /** Saves the current shared note as a new local note in the user's own library. */
    fun saveCopy() {
        viewModelScope.launch {
            val snapshot = _state.value
            val now = System.currentTimeMillis()
            val note = Note(
                title = snapshot.title,
                content = plainToHtml(snapshot.content),
                createdAt = now,
                lastEdited = now,
                color = 0
            )
            runCatching { noteRepository.insertNote(note) }
                .onSuccess {
                    _state.update { it.copy(savedLocally = true) }
                    _events.tryEmit(SharedNoteEvent.SavedCopy)
                    _events.tryEmit(SharedNoteEvent.Toast("Saved to your notes"))
                }
                .onFailure { _events.tryEmit(SharedNoteEvent.Toast("Couldn't save a copy")) }
        }
    }

    fun retry() {
        val id = shareId ?: return
        load(id)
    }

    override fun onCleared() {
        super.onCleared()
        session?.close()
        session = null
    }
}

/** Escapes plain text and converts newlines to <br> so it renders on the web share page. */
private fun plainToHtml(text: String): String =
    text.replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\n", "<br>")
