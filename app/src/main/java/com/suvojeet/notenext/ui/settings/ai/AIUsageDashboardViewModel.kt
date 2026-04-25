package com.suvojeet.notenext.ui.settings.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suvojeet.notenext.data.ai.AIFeature
import com.suvojeet.notenext.data.ai.AIUsageRepository
import com.suvojeet.notenext.data.ai.FeatureStatsRow
import com.suvojeet.notenext.data.ai.ProviderUsageRow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AIUsageDashboardViewModel @Inject constructor(
    private val repository: AIUsageRepository
) : ViewModel() {

    val total: StateFlow<Int> = repository.observeTotalCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val successCount: StateFlow<Int> = repository.observeSuccessCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val acceptedCount: StateFlow<Int> = repository.observeAcceptedCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val suggestionTotal: StateFlow<Int> = repository.observeSuggestionTotal()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val featureStats: StateFlow<List<FeatureStatsRow>> = repository.observeFeatureStats()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val providerStats: StateFlow<List<ProviderUsageRow>> = repository.observePerProvider()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val helpful: StateFlow<HelpfulnessSummary> = combine(
        repository.observeAcceptedCount(),
        repository.observeSuggestionTotal()
    ) { accepted, total ->
        HelpfulnessSummary(
            acceptedCount = accepted,
            suggestionTotal = total,
            ratePercent = if (total > 0) (accepted * 100 / total) else 0
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HelpfulnessSummary(0, 0, 0))

    fun clearAll() {
        viewModelScope.launch { repository.clearAll() }
    }

    /** Used by the bar chart — feature → total invocation count. */
    val perFeatureTotals: StateFlow<Map<AIFeature, Int>> = repository.observePerFeature()
        .map { rows ->
            rows.mapNotNull { row ->
                AIFeature.fromId(row.featureId)?.let { it to row.total }
            }.toMap()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())
}

data class HelpfulnessSummary(
    val acceptedCount: Int,
    val suggestionTotal: Int,
    val ratePercent: Int
)
