package com.suvojeet.notenext.ui.settings.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suvojeet.notenext.data.ai.AIFeature
import com.suvojeet.notenext.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AIFeaturesViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val masterEnabled: StateFlow<Boolean> = settingsRepository.aiMasterEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val states: StateFlow<Map<AIFeature, Boolean>> = combine(
        settingsRepository.aiFeatureSummarize,
        settingsRepository.aiFeatureChecklist,
        settingsRepository.aiFeatureTodos,
        settingsRepository.aiFeatureGrammar,
        settingsRepository.aiFeatureAutoTag,
        settingsRepository.aiFeatureSmartReminder,
        settingsRepository.aiFeatureLinkedNotes,
        settingsRepository.aiFeatureToneRewrite,
        settingsRepository.aiFeatureCustomPrompt
    ) { values: Array<Boolean> ->
        mapOf(
            AIFeature.SUMMARIZE to values[0],
            AIFeature.CHECKLIST to values[1],
            AIFeature.TODOS to values[2],
            AIFeature.GRAMMAR to values[3],
            AIFeature.AUTO_TAG to values[4],
            AIFeature.SMART_REMINDER to values[5],
            AIFeature.LINKED_NOTES to values[6],
            AIFeature.TONE_REWRITE to values[7],
            AIFeature.CUSTOM_PROMPT to values[8]
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    fun toggle(feature: AIFeature, enabled: Boolean) {
        viewModelScope.launch {
            when (feature) {
                AIFeature.SUMMARIZE -> settingsRepository.saveAiFeatureSummarize(enabled)
                AIFeature.CHECKLIST -> settingsRepository.saveAiFeatureChecklist(enabled)
                AIFeature.TODOS -> settingsRepository.saveAiFeatureTodos(enabled)
                AIFeature.GRAMMAR -> settingsRepository.saveAiFeatureGrammar(enabled)
                AIFeature.AUTO_TAG -> settingsRepository.saveAiFeatureAutoTag(enabled)
                AIFeature.SMART_REMINDER -> settingsRepository.saveAiFeatureSmartReminder(enabled)
                AIFeature.LINKED_NOTES -> settingsRepository.saveAiFeatureLinkedNotes(enabled)
                AIFeature.TONE_REWRITE -> settingsRepository.saveAiFeatureToneRewrite(enabled)
                AIFeature.CUSTOM_PROMPT -> settingsRepository.saveAiFeatureCustomPrompt(enabled)
            }
        }
    }

    fun enableAll() {
        viewModelScope.launch {
            settingsRepository.saveAiFeatureSummarize(true)
            settingsRepository.saveAiFeatureChecklist(true)
            settingsRepository.saveAiFeatureTodos(true)
            settingsRepository.saveAiFeatureGrammar(true)
            settingsRepository.saveAiFeatureAutoTag(true)
            settingsRepository.saveAiFeatureSmartReminder(true)
            settingsRepository.saveAiFeatureLinkedNotes(true)
            settingsRepository.saveAiFeatureToneRewrite(true)
            settingsRepository.saveAiFeatureCustomPrompt(true)
        }
    }

    fun disableAll() {
        viewModelScope.launch {
            settingsRepository.saveAiFeatureSummarize(false)
            settingsRepository.saveAiFeatureChecklist(false)
            settingsRepository.saveAiFeatureTodos(false)
            settingsRepository.saveAiFeatureGrammar(false)
            settingsRepository.saveAiFeatureAutoTag(false)
            settingsRepository.saveAiFeatureSmartReminder(false)
            settingsRepository.saveAiFeatureLinkedNotes(false)
            settingsRepository.saveAiFeatureToneRewrite(false)
            settingsRepository.saveAiFeatureCustomPrompt(false)
        }
    }
}
