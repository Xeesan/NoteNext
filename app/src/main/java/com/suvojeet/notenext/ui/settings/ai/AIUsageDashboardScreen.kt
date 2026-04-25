@file:OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
package com.suvojeet.notenext.ui.settings.ai

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.suvojeet.notenext.data.ai.AIFeature
import com.suvojeet.notenext.ui.components.ExpressiveSection
import com.suvojeet.notenext.ui.components.SettingsGroupCard
import com.suvojeet.notenext.ui.components.springPress

/**
 * Local AI usage dashboard.
 *
 * Shown:
 *  - Hero: total invocations + success rate
 *  - Suggestion helpfulness card (acceptance rate)
 *  - Per-feature bar chart
 *  - Per-feature detail rows (count, success rate, avg latency, acceptance rate)
 *  - Per-provider breakdown
 *  - Clear-all action (with confirmation)
 *
 * No data ever leaves the device. Source: ai_usage_events Room table.
 */
@Composable
fun AIUsageDashboardScreen(
    onBackClick: () -> Unit,
    viewModel: AIUsageDashboardViewModel = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val total by viewModel.total.collectAsStateWithLifecycle()
    val success by viewModel.successCount.collectAsStateWithLifecycle()
    val featureStats by viewModel.featureStats.collectAsStateWithLifecycle()
    val providerStats by viewModel.providerStats.collectAsStateWithLifecycle()
    val helpful by viewModel.helpful.collectAsStateWithLifecycle()
    val perFeature by viewModel.perFeatureTotals.collectAsStateWithLifecycle()

    var showClearConfirm by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "Usage",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-1.0).sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick, modifier = Modifier.springPress()) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (total > 0) {
                        IconButton(
                            onClick = { showClearConfirm = true },
                            modifier = Modifier.springPress()
                        ) {
                            Icon(Icons.Rounded.DeleteSweep, contentDescription = "Clear")
                        }
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->
        if (total == 0) {
            EmptyState(modifier = Modifier.padding(padding))
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    HeroCard(
                        total = total,
                        success = success
                    )
                }

                if (helpful.suggestionTotal > 0) {
                    item { HelpfulnessCard(summary = helpful) }
                }

                item {
                    ExpressiveSection(
                        title = "By feature",
                        description = "How often each AI feature has run on this device"
                    ) {
                        SettingsGroupCard {
                            Column(modifier = Modifier.padding(16.dp)) {
                                BarChart(perFeature)
                            }
                        }
                    }
                }

                if (featureStats.isNotEmpty()) {
                    item {
                        ExpressiveSection(
                            title = "Details",
                            description = "Success rate, average latency, acceptance"
                        ) {
                            SettingsGroupCard {
                                featureStats.forEachIndexed { i, row ->
                                    val feature = AIFeature.fromId(row.featureId) ?: return@forEachIndexed
                                    FeatureDetailRow(feature = feature, row = row)
                                    if (i < featureStats.lastIndex) {
                                        HorizontalDivider(
                                            modifier = Modifier.padding(horizontal = 16.dp),
                                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                if (providerStats.isNotEmpty()) {
                    item {
                        ExpressiveSection(
                            title = "By provider",
                            description = "Which AI service handled your requests"
                        ) {
                            SettingsGroupCard {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    val totalProvider = providerStats.sumOf { it.total }
                                    providerStats.forEach { p ->
                                        ProviderRow(
                                            name = p.provider,
                                            count = p.total,
                                            fraction = if (totalProvider > 0) p.total.toFloat() / totalProvider else 0f
                                        )
                                        Spacer(Modifier.height(8.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                item { Spacer(Modifier.height(32.dp)) }
            }
        }
    }

    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            shape = MaterialTheme.shapes.extraLarge,
            title = { Text("Clear AI usage history?") },
            text = { Text("This permanently deletes the local usage stats shown on this dashboard. Your notes and AI feature settings are unaffected.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearAll()
                    showClearConfirm = false
                }, modifier = Modifier.springPress()) {
                    Text("Clear", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirm = false }, modifier = Modifier.springPress()) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize().padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Rounded.QueryStats,
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Spacer(Modifier.height(16.dp))
            Text(
                "No AI activity yet",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Once you enable an AI feature and use it, you'll see how often it ran, how fast it was, and how often you accepted its suggestions — all stored locally on this device.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun HeroCard(total: Int, success: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                "AI invocations",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                total.toString(),
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(Modifier.height(12.dp))
            Row {
                StatPill(
                    label = "Success",
                    value = "$success",
                    icon = Icons.Rounded.CheckCircle,
                    color = Color(0xFF388E3C)
                )
                Spacer(Modifier.width(8.dp))
                val rate = if (total > 0) success * 100 / total else 0
                StatPill(
                    label = "Success rate",
                    value = "$rate%",
                    icon = Icons.Rounded.Bolt,
                    color = Color(0xFFFFB300)
                )
            }
        }
    }
}

@Composable
private fun StatPill(label: String, value: String, icon: ImageVector, color: Color) {
    Surface(
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(8.dp))
            Column {
                Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(value, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun HelpfulnessCard(summary: HelpfulnessSummary) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(MaterialTheme.shapes.large)
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Rounded.ThumbUp,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Suggestions you accepted",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Text(
                    "${summary.ratePercent}%  ·  ${summary.acceptedCount} of ${summary.suggestionTotal}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }
    }
}

@Composable
private fun BarChart(data: Map<AIFeature, Int>) {
    if (data.isEmpty()) {
        Text("No data yet", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        return
    }
    val max = (data.values.maxOrNull() ?: 1).coerceAtLeast(1)
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        data.entries.sortedByDescending { it.value }.forEach { (feature, count) ->
            BarRow(label = feature.displayName, count = count, max = max)
        }
    }
}

@Composable
private fun BarRow(label: String, count: Int, max: Int) {
    val fraction = (count.toFloat() / max).coerceIn(0f, 1f)
    val animFraction by animateFloatAsState(targetValue = fraction, animationSpec = spring(), label = "bar")

    Column {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                label,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                count.toString(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animFraction)
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}

@Composable
private fun FeatureDetailRow(
    feature: AIFeature,
    row: com.suvojeet.notenext.data.ai.FeatureStatsRow
) {
    val successRate = if (row.total > 0) row.successes * 100 / row.total else 0
    val avgLatency = if (row.avgDurationMs > 0) "${row.avgDurationMs.toInt()}ms" else "—"
    val acceptanceText = if (row.suggestions > 0) {
        val pct = row.accepted * 100 / row.suggestions
        "$pct% accepted (${row.accepted}/${row.suggestions})"
    } else "—"

    Column(modifier = Modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                feature.displayName,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            AssistChip(
                onClick = {},
                label = { Text("${row.total} runs", style = MaterialTheme.typography.labelSmall) }
            )
        }
        Spacer(Modifier.height(6.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            DetailStat(label = "Success", value = "$successRate%")
            DetailStat(label = "Avg latency", value = avgLatency)
            if (feature.isSuggestionFeature) {
                DetailStat(label = "Accepted", value = acceptanceText)
            }
        }
    }
}

@Composable
private fun DetailStat(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun ProviderRow(name: String, count: Int, fraction: Float) {
    val animFraction by animateFloatAsState(targetValue = fraction, animationSpec = spring(), label = "providerBar")
    Column {
        Row {
            Text(
                name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )
            Text("$count", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animFraction)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.tertiary)
            )
        }
    }
}
