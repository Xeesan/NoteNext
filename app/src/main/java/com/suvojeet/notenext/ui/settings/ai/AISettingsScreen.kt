@file:OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
package com.suvojeet.notenext.ui.settings.ai

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.suvojeet.notenext.data.ai.AIProvider
import com.suvojeet.notenext.ui.components.ExpressiveSection
import com.suvojeet.notenext.ui.components.SettingsGroupCard
import com.suvojeet.notenext.ui.components.springPress

/**
 * Unified AI Settings hub. Replaces the legacy AIProviderSettingsScreen and
 * GroqSettingsScreen — everything lives here now:
 *
 *   1. Master kill-switch (default OFF for privacy).
 *   2. Privacy banner explaining what AI is and what data leaves the device.
 *   3. Quick links to "Features" and "Usage Dashboard" subscreens.
 *   4. Provider picker + per-provider key/model configuration.
 *   5. Local usage-tracking toggle.
 */
@Composable
fun AISettingsScreen(
    onBackClick: () -> Unit,
    onOpenFeatures: () -> Unit,
    onOpenOnDeviceFeatures: () -> Unit,
    onOpenDashboard: () -> Unit,
    viewModel: AISettingsViewModel = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val masterEnabled by viewModel.masterEnabled.collectAsStateWithLifecycle()
    val usageTracking by viewModel.usageTrackingEnabled.collectAsStateWithLifecycle()
    val totalCalls by viewModel.totalInvocations.collectAsStateWithLifecycle()
    val selectedProvider by viewModel.selectedProvider.collectAsStateWithLifecycle()
    val availableModels by viewModel.availableModels.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshingModels.collectAsStateWithLifecycle()

    val groqUseCustom by viewModel.useCustomGroqKey.collectAsStateWithLifecycle()
    val groqKey by viewModel.groqCustomKey.collectAsStateWithLifecycle()
    val groqFast by viewModel.groqFastModel.collectAsStateWithLifecycle()
    val groqLarge by viewModel.groqLargeModel.collectAsStateWithLifecycle()

    val openAIKey by viewModel.openAIKey.collectAsStateWithLifecycle()
    val openAIBase by viewModel.openAIBaseUrl.collectAsStateWithLifecycle()
    val openAIModel by viewModel.openAIModel.collectAsStateWithLifecycle()

    val anthropicKey by viewModel.anthropicKey.collectAsStateWithLifecycle()
    val anthropicModel by viewModel.anthropicModel.collectAsStateWithLifecycle()

    val geminiKey by viewModel.geminiKey.collectAsStateWithLifecycle()
    val geminiModel by viewModel.geminiModel.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "AI",
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
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item { PrivacyBanner() }

            item {
                ExpressiveSection(
                    title = "Master switch",
                    description = "Turn every AI feature on or off. NoteNext is offline-first — AI is opt-in."
                ) {
                    SettingsGroupCard {
                        SettingsToggle(
                            icon = if (masterEnabled) Icons.Rounded.AutoAwesome else Icons.Rounded.PowerSettingsNew,
                            title = if (masterEnabled) "AI is enabled" else "AI is disabled",
                            subtitle = if (masterEnabled)
                                "Per-feature toggles below take effect"
                            else
                                "All AI features are off — nothing will be sent to any provider",
                            iconColor = if (masterEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            checked = masterEnabled,
                            onCheckedChange = viewModel::setMasterEnabled
                        )
                    }
                }
            }

            item {
                ExpressiveSection(
                    title = "Manage",
                    description = "Pick which features run, see what they're doing"
                ) {
                    SettingsGroupCard {
                        SettingsNav(
                            icon = Icons.Rounded.Tune,
                            iconColor = MaterialTheme.colorScheme.primary,
                            title = "AI Features",
                            subtitle = "Enable or disable each feature individually",
                            onClick = onOpenFeatures
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )
                        SettingsNav(
                            icon = Icons.Rounded.Devices,
                            iconColor = Color(0xFF00897B),
                            title = "On-Device Features",
                            subtitle = "Smart features that run locally without AI",
                            onClick = onOpenOnDeviceFeatures
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )
                        SettingsNav(
                            icon = Icons.Rounded.Insights,
                            iconColor = Color(0xFF6750A4),
                            title = "Usage Dashboard",
                            subtitle = if (totalCalls == 0) "No AI calls yet" else "$totalCalls AI invocations recorded",
                            onClick = onOpenDashboard
                        )
                    }
                }
            }

            item {
                ExpressiveSection(
                    title = "Provider",
                    description = "Choose which service handles your AI requests. Default provider is built in — others need an API key."
                ) {
                    SettingsGroupCard {
                        Column(modifier = Modifier.padding(16.dp)) {
                            ProviderCard(
                                name = "AI Provider",
                                description = "Fast inference, bundled with the app",
                                icon = Icons.Rounded.Bolt,
                                iconColor = Color(0xFFFFC107),
                                isSelected = selectedProvider == AIProvider.GROQ,
                                badge = "Default",
                                onClick = { viewModel.selectProvider(AIProvider.GROQ) }
                            )
                            Spacer(Modifier.height(12.dp))
                            ProviderCard(
                                name = "OpenAI",
                                description = "GPT-4o, GPT-4.1 — bring your own key",
                                icon = Icons.Rounded.AutoAwesome,
                                iconColor = Color(0xFF10A37A),
                                isSelected = selectedProvider == AIProvider.OPENAI,
                                badge = if (openAIKey.isNotBlank()) "Configured" else null,
                                onClick = { viewModel.selectProvider(AIProvider.OPENAI) }
                            )
                            Spacer(Modifier.height(12.dp))
                            ProviderCard(
                                name = "Anthropic (Claude)",
                                description = "Claude Sonnet, Haiku, Opus — bring your own key",
                                icon = Icons.Rounded.Psychology,
                                iconColor = Color(0xFFFF5722),
                                isSelected = selectedProvider == AIProvider.ANTHROPIC,
                                badge = if (anthropicKey.isNotBlank()) "Configured" else null,
                                onClick = { viewModel.selectProvider(AIProvider.ANTHROPIC) }
                            )
                            Spacer(Modifier.height(12.dp))
                            ProviderCard(
                                name = "Google Gemini",
                                description = "Gemini Flash & Pro — bring your own key",
                                icon = Icons.Rounded.ModelTraining,
                                iconColor = Color(0xFF4285F4),
                                isSelected = selectedProvider == AIProvider.GEMINI,
                                badge = if (geminiKey.isNotBlank()) "Configured" else null,
                                onClick = { viewModel.selectProvider(AIProvider.GEMINI) }
                            )
                        }
                    }
                }
            }

            // Per-provider configuration (only the active one is shown to keep things tidy)
            when (selectedProvider) {
                AIProvider.GROQ -> item {
                    GroqConfigSection(
                        useCustomKey = groqUseCustom,
                        customKey = groqKey,
                        availableModels = availableModels[AIProvider.GROQ].orEmpty(),
                        fastModel = groqFast,
                        largeModel = groqLarge,
                        onSetUseCustomKey = viewModel::setUseCustomGroqKey,
                        onSaveKey = viewModel::saveGroqKey,
                        onRefreshModels = { viewModel.refreshModels() },
                        isRefreshing = isRefreshing,
                        onSetFastModel = viewModel::saveGroqFastModel,
                        onSetLargeModel = viewModel::saveGroqLargeModel
                    )
                }
                AIProvider.OPENAI -> item {
                    OpenAIConfigSection(
                        apiKey = openAIKey,
                        baseUrl = openAIBase,
                        selectedModel = openAIModel,
                        availableModels = availableModels[AIProvider.OPENAI].orEmpty(),
                        onSaveKey = viewModel::saveOpenAIKey,
                        onSaveBaseUrl = viewModel::saveOpenAIBaseUrl,
                        onSelectModel = viewModel::saveOpenAIModel,
                        onRefreshModels = { viewModel.refreshModels() },
                        isRefreshing = isRefreshing
                    )
                }
                AIProvider.ANTHROPIC -> item {
                    AnthropicConfigSection(
                        apiKey = anthropicKey,
                        selectedModel = anthropicModel,
                        availableModels = availableModels[AIProvider.ANTHROPIC].orEmpty(),
                        onSaveKey = viewModel::saveAnthropicKey,
                        onSelectModel = viewModel::saveAnthropicModel
                    )
                }
                AIProvider.GEMINI -> item {
                    GeminiConfigSection(
                        apiKey = geminiKey,
                        selectedModel = geminiModel,
                        availableModels = availableModels[AIProvider.GEMINI].orEmpty(),
                        onSaveKey = viewModel::saveGeminiKey,
                        onSelectModel = viewModel::saveGeminiModel
                    )
                }
                else -> {}
            }

            item {
                ExpressiveSection(
                    title = "Privacy",
                    description = "Local-only telemetry — nothing here ever leaves your device"
                ) {
                    SettingsGroupCard {
                        SettingsToggle(
                            icon = Icons.Rounded.QueryStats,
                            title = "Track AI usage locally",
                            subtitle = "Powers the dashboard. Counts which features ran, when, and the result. The note content is never recorded.",
                            iconColor = Color(0xFF00897B),
                            checked = usageTracking,
                            onCheckedChange = viewModel::setUsageTrackingEnabled
                        )
                    }
                }
            }

            item { Spacer(Modifier.height(32.dp)) }
        }
    }
}

@Composable
private fun PrivacyBanner() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                Icons.Rounded.Shield,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.size(28.dp)
            )
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    text = "How AI works in NoteNext",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Every AI feature is OFF by default. When you turn one on, the relevant text from your note is sent to the provider you've chosen — and nothing else. We never upload your full database, attachments, or labels in bulk. You can disable everything with one tap above.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }
    }
}

@Composable
private fun ProviderCard(
    name: String,
    description: String,
    icon: ImageVector,
    iconColor: Color,
    isSelected: Boolean,
    badge: String?,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .springPress(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            } else {
                MaterialTheme.colorScheme.surfaceContainerLow
            }
        ),
        border = if (isSelected) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else null
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(iconColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (badge != null) {
                        Spacer(Modifier.width(8.dp))
                        AssistChip(
                            onClick = onClick,
                            label = { Text(badge, style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier.height(24.dp)
                        )
                    }
                }
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (isSelected) {
                Icon(
                    Icons.Rounded.CheckCircle,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
internal fun SettingsToggle(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconColor: Color,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    ListItem(
        modifier = Modifier.springPress(),
        headlineContent = { Text(title, fontWeight = FontWeight.SemiBold) },
        supportingContent = {
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        },
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(iconColor.copy(alpha = 0.12f), MaterialTheme.shapes.medium),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconColor)
            }
        },
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                thumbContent = if (checked) {
                    { Icon(Icons.Rounded.Check, contentDescription = null, modifier = Modifier.size(SwitchDefaults.IconSize)) }
                } else null
            )
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}

@Composable
internal fun SettingsNav(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    ListItem(
        modifier = Modifier
            .springPress()
            .clickable(onClick = onClick),
        headlineContent = { Text(title, fontWeight = FontWeight.SemiBold) },
        supportingContent = {
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        },
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(iconColor.copy(alpha = 0.12f), MaterialTheme.shapes.medium),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconColor)
            }
        },
        trailingContent = {
            Icon(
                Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            )
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}
