package com.suvojeet.notenext.data.share

import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

/** Opens real-time collaboration sessions against the NoteNext Socket.IO backend. */
@Singleton
class CollaborationManager @Inject constructor() {
    /** Creates (but does not connect) a session for the given note. Call [CollabSession.connect]. */
    fun open(shareId: String): CollabSession = CollabSession(ShareConstants.BASE_URL, shareId)
}

/**
 * A single collaborative editing session. Joins the note's room, relays local
 * edits via `edit-note`, and surfaces peers' edits via [incoming]. The server
 * persists edits (debounced) so late joiners and the web page stay in sync.
 *
 * Not thread-safe across multiple connect/close cycles — create one per note
 * and [close] it when done.
 */
class CollabSession(
    private val serverUrl: String,
    private val shareId: String
) {
    private var socket: Socket? = null

    private val _incoming = MutableSharedFlow<NoteDelta>(
        extraBufferCapacity = 32,
        replay = 0
    )
    val incoming: SharedFlow<NoteDelta> = _incoming.asSharedFlow()

    private val _connected = MutableStateFlow(false)
    val connected: StateFlow<Boolean> = _connected.asStateFlow()

    fun connect() {
        if (socket != null) return
        // Guard the entire setup against Throwable (not just Exception): a malformed
        // URL throws, and a transport/library incompatibility could surface as a
        // LinkageError. If live collaboration can't start, the session simply stays
        // "disconnected" and edits fall back to REST persistence.
        try {
            val s = IO.socket(serverUrl)
            socket = s

            // Event names are passed as literals (their stable wire values) to avoid
            // coupling to specific client-constant names across library versions.
            s.on("connect") {
                _connected.value = true
                s.emit("join-note", shareId)
            }
            s.on("disconnect") { _connected.value = false }
            s.on("connect_error") { _connected.value = false }

            s.on("note-updated") { args ->
                val obj = args.getOrNull(0) as? JSONObject ?: return@on
                val title = if (obj.has("title")) obj.optString("title") else null
                val content = if (obj.has("content")) obj.optString("content") else null
                _incoming.tryEmit(NoteDelta(title = title, content = content))
            }

            s.connect()
        } catch (t: Throwable) {
            socket = null
            _connected.value = false
        }
    }

    /** Broadcasts the current title/content to collaborators (and triggers server persistence). */
    fun sendEdit(title: String?, content: String?) {
        val s = socket ?: return
        val delta = JSONObject().apply {
            if (title != null) put("title", title)
            if (content != null) put("content", content)
        }
        val payload = JSONObject().apply {
            put("noteId", shareId)
            put("delta", delta)
        }
        s.emit("edit-note", payload)
    }

    fun close() {
        socket?.let { s ->
            try { s.emit("leave-note", shareId) } catch (_: Exception) {}
            s.off()
            s.disconnect()
        }
        socket = null
        _connected.value = false
    }
}
