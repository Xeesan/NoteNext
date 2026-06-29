package com.suvojeet.notenext.data.share

import retrofit2.http.Body
import retrofit2.http.GET
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
}
