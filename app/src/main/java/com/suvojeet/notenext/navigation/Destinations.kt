package com.suvojeet.notenext.navigation

import kotlinx.serialization.Serializable

import com.suvojeet.notenext.core.model.NoteType

@Serializable
sealed interface Destination {
    @Serializable
    data class Notes(val noteId: Int = -1) : Destination
    
    @Serializable
    data object Projects : Destination
    
    @Serializable
    data class ProjectNotes(val projectId: Int) : Destination
    
    @Serializable
    data object Archive : Destination
    
    @Serializable
    data object Bin : Destination
    
    @Serializable
    data object Settings : Destination

    @Serializable
    data object PrivacySecurity : Destination

    @Serializable
    data object Backup : Destination    
    @Serializable
    data object EditLabels : Destination
    
    @Serializable
    data object Reminder : Destination
    
    @Serializable
    data object AddEditReminder : Destination
    
    @Serializable
    data object About : Destination

    @Serializable
    data object Contact : Destination

    @Serializable
    data object Credits : Destination

    @Serializable
    data object Changelog : Destination

    @Serializable
    data object Donate : Destination
    
    @Serializable
    data object GroqSettings : Destination

    @Serializable
    data object AIProviderSettings : Destination

    @Serializable
    data object AISettings : Destination

    @Serializable
    data object AIFeatures : Destination

    @Serializable
    data object OnDeviceFeatures : Destination

    @Serializable
    data object AIUsageDashboard : Destination

    @Serializable
    data object Todo : Destination
    
    @Serializable
    data class AddEditNote(val projectId: Int = -1, val noteType: NoteType = NoteType.TEXT, val externalUri: String? = null) : Destination

    @Serializable
    data object Drawing : Destination

    /** A shared/collaborative note opened from a share link (https://…/s/<id> or notenext://note/<id>). */
    @Serializable
    data class SharedNote(val shareId: String) : Destination
}
