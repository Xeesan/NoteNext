package com.suvojeet.notenext.data.share

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.POST
import retrofit2.http.Path

/** Retrofit interface for the NoteNext sharing/collaboration backend. */
interface NoteNextApiService {

    @POST("api/notes/share")
    suspend fun shareNote(@Body body: ShareNoteRequest): ShareNoteResponse

    @GET("api/notes/{shareId}")
    suspend fun getNote(@Path("shareId") shareId: String): SharedNoteDto

    @PUT("api/notes/{shareId}")
    suspend fun updateNote(@Path("shareId") shareId: String, @Body body: ShareNoteRequest): SharedNoteDto

    /**
     * Delete (unshare) a note. The backend authorizes via the secret delete-token
     * (creator-only), supplied in the x-delete-token header — a public shareId
     * alone cannot delete a note.
     */
    @DELETE("api/notes/{shareId}")
    suspend fun deleteNote(
        @Path("shareId") shareId: String,
        @Header("x-delete-token") deleteToken: String
    )
}
