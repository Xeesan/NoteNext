package com.suvojeet.notenext.di

import com.suvojeet.notenext.BuildConfig
import com.suvojeet.notenext.data.share.NoteNextApiService
import com.suvojeet.notenext.data.share.ShareConstants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * Qualifier for the NoteNext sharing backend's Retrofit/OkHttp graph. Keeps it
 * separate from the Groq client in [NetworkModule], whose interceptor injects a
 * Groq API key on every request — something we must NOT send to our own server.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NoteNextBackend

@Module
@InstallIn(SingletonComponent::class)
object ShareModule {

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    @Provides
    @Singleton
    @NoteNextBackend
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @NoteNextBackend
    fun provideRetrofit(@NoteNextBackend client: OkHttpClient): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(ShareConstants.API_BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    @Provides
    @Singleton
    fun provideNoteNextApiService(@NoteNextBackend retrofit: Retrofit): NoteNextApiService =
        retrofit.create(NoteNextApiService::class.java)
}
