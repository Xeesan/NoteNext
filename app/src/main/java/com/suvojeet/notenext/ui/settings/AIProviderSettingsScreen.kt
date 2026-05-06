@file:OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
package com.suvojeet.notenext.ui.settings

import androidx.lifecycle.compose.collectAsStateWithLifecycle

import androidx.compose.animation.*
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.suvojeet.notenext.R
import com.suvojeet.notenext.data.ai.AIProvider
import com.suvojeet.notenext.ui.components.ExpressiveSection
import com.suvojeet.notenext.ui.components.SettingsGroupCard
import com.suvojeet.notenext.ui.components.springPress

@Composable
fun AIProviderSettingsScreen(
    onBackClick: () -> Unit,
    viewModel: AIProviderSettingsViewModel = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val selectedProvider by viewModel.selectedProvider.collectAsStateWithLifecycle(initialValue = AIProvider.GROQ)
    val groqKey by viewModel.groqKey.collectAsStateWithLifecycle(initialValue = "")
    val openaiKey by viewModel.openaiApiKey.collectAsStateWithLifecycle(initialValue = "")
    val openaiBaseUrl by viewModel.openaiBaseUrl.collectAsStateWithLifecycle(initialValue = "https://api.openai.com/")
    val anthropicKey by viewModel.anthropicApiKey.collectAsStateWithLifecycle(initialValue = "")
    val geminiKey by viewModel.geminiApiKey.collectAsStateWithLifecycle(initialValue = "")
    
    val openaiModels by viewModel.openaiModels.collectAsStateWithLifecycle()
    val anthropicModels by viewModel.anthropicModels.collectAsStateWithLifecycle()
    val geminiModels by viewModel.geminiModels.collectAsStateWithLifecycle()
    val selectedOpenAIModel by viewModel.selectedOpenAIModel.collectAsStateWithLifecycle(initialValue = "gpt-4o-mini")
    val selectedAnthropicModel by viewModel.selectedAnthropicModel.collectAsStateWithLifecycle(initialValue = "claude-3-5-sonnet-20241022")
    val selectedGeminiModel by viewModel.selectedGeminiModel.collectAsStateWithLifecycle(initialValue = "gemini-3.1-flash")
    val isLoadingModels by viewModel.isLoadingModels.collectAsStateWithLifecycle()

    var showGroqKeyVisible by remember { mutableStateOf(false) }
    var showOpenaiKeyVisible by remember { mutableStateOf(false) }
    var showAnthropicKeyVisible by remember { mutableStateOf(false) }
    var showGeminiKeyVisible by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.ai_providers_title),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-1.0).sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick, modifier = Modifier.springPress()) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = stringResource(id = R.string.back))
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                ExpressiveSection(
                    title = stringResource(id = R.string.ai_select_provider_title),
                    description = stringResource(id = R.string.ai_select_provider_description)
                ) {
                    SettingsGroupCard {
                        Column(modifier = Modifier.padding(16.dp)) {
                            AIProviderCard(
                                provider = AIProvider.GROQ,
                                name = "Groq",
                                description = stringResource(id = R.string.ai_provider_groq_desc),
                                icon = Icons.Rounded.Bolt,
                                iconColor = Color(0xFFFFC107),
                                isSelected = selectedProvider == AIProvider.GROQ,
                                onClick = { viewModel.selectProvider(AIProvider.GROQ) }
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            AIProviderCard(
                                provider = AIProvider.OPENAI,
                                name = "OpenAI",
                                description = stringResource(id = R.string.ai_provider_openai_desc),
                                icon = Icons.Rounded.AutoAwesome,
                                iconColor = Color(0xFF10A37A),
                                isSelected = selectedProvider == AIProvider.OPENAI,
                                onClick = { viewModel.selectProvider(AIProvider.OPENAI) }
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            AIProviderCard(
                                provider = AIProvider.ANTHROPIC,
                                name = stringResource(id = R.string.ai_provider_anthropic_name),
                                description = stringResource(id = R.string.ai_provider_anthropic_desc),
                                icon = Icons.Rounded.Psychology,
                                iconColor = Color(0xFFFF5722),
                                isSelected = selectedProvider == AIProvider.ANTHROPIC,
                                onClick = { viewModel.selectProvider(AIProvider.ANTHROPIC) }
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            AIProviderCard(
                                provider = AIProvider.GEMINI,
                                name = stringResource(id = R.string.ai_provider_gemini_name),
                                description = stringResource(id = R.string.ai_provider_gemini_desc),
                                icon = Icons.Rounded.ModelTraining,
                                iconColor = Color(0xFF4285F4),
                                isSelected = selectedProvider == AIProvider.GEMINI,
                                onClick = { viewModel.selectProvider(AIProvider.GEMINI) }
                            )
                        }
                    }
                }
            }

            // Groq Configuration
            if (selectedProvider == AIProvider.GROQ) {
                item {
                    ExpressiveSection(
                        title = stringResource(id = R.string.ai_groq_config_title),
                        description = stringResource(id = R.string.ai_groq_config_description)
                    ) {
                        SettingsGroupCard {
                            Column(modifier = Modifier.padding(16.dp)) {
                                OutlinedTextField(
                                    value = groqKey,
                                    onValueChange = { viewModel.saveGroqKey(it) },
                                    label = { Text(stringResource(id = R.string.ai_groq_key_label)) },
                                    placeholder = { Text("gsk_...") },
                                    modifier = Modifier.fillMaxWidth(),
                                    visualTransformation = if (showGroqKeyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                    trailingIcon = {
                                        IconButton(onClick = { showGroqKeyVisible = !showGroqKeyVisible }) {
                                            Icon(
                                                imageVector = if (showGroqKeyVisible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                                                contentDescription = null
                                            )
                                        }
                                    },
                                    shape = MaterialTheme.shapes.medium
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = stringResource(id = R.string.ai_groq_key_help),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = { onBackClick() }, // Redirect to Groq settings or add model selection here too
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = MaterialTheme.shapes.medium
                                ) {
                                    Icon(Icons.Rounded.Settings, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(stringResource(id = R.string.ai_groq_advanced_settings))
                                }
                            }
                        }
                    }
                }
            }

            // OpenAI Configuration
            if (selectedProvider == AIProvider.OPENAI) {
                item {
                    ExpressiveSection(
                        title = stringResource(id = R.string.ai_openai_config_title),
                        description = stringResource(id = R.string.ai_openai_config_description)
                    ) {
                        SettingsGroupCard {
                            Column(modifier = Modifier.padding(16.dp)) {
                                OutlinedTextField(
                                    value = openaiBaseUrl,
                                    onValueChange = { viewModel.saveOpenaiBaseUrl(it) },
                                    label = { Text(stringResource(id = R.string.ai_openai_base_url_label)) },
                                    placeholder = { Text("https://api.openai.com/") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = MaterialTheme.shapes.medium
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedTextField(
                                    value = openaiKey,
                                    onValueChange = { viewModel.saveOpenaiKey(it) },
                                    label = { Text(stringResource(id = R.string.ai_openai_key_label)) },
                                    placeholder = { Text("sk-...") },
                                    modifier = Modifier.fillMaxWidth(),
                                    visualTransformation = if (showOpenaiKeyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                    trailingIcon = {
                                        IconButton(onClick = { showOpenaiKeyVisible = !showOpenaiKeyVisible }) {
                                            Icon(
                                                imageVector = if (showOpenaiKeyVisible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                                                contentDescription = null
                                            )
                                        }
                                    },
                                    shape = MaterialTheme.shapes.medium
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = stringResource(id = R.string.ai_openai_key_help),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                
                                if (openaiKey.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Button(
                                        onClick = { viewModel.refreshModels() },
                                        enabled = !isLoadingModels,
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = MaterialTheme.shapes.medium
                                    ) {
                                        if (isLoadingModels) {
                                            LoadingIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(stringResource(id = R.string.ai_fetching_models))
                                        } else {
                                            Icon(Icons.Rounded.Refresh, contentDescription = null)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(stringResource(id = R.string.ai_refresh_models))
                                        }
                                    }
                                }
                            }
                            
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                            ModelSelector(
                                title = stringResource(id = R.string.ai_select_openai_model),
                                subtitle = stringResource(id = R.string.ai_model_selector_subtitle),
                                selectedModel = selectedOpenAIModel,
                                availableModels = openaiModels,
                                onModelSelected = { viewModel.selectOpenAIModel(it) },
                                icon = Icons.Rounded.AutoAwesome,
                                iconColor = Color(0xFF10A37A)
                            )
                        }
                    }
                }
            }

            // Anthropic Configuration
            if (selectedProvider == AIProvider.ANTHROPIC) {
                item {
                    ExpressiveSection(
                        title = stringResource(id = R.string.ai_anthropic_config_title),
                        description = stringResource(id = R.string.ai_anthropic_config_description)
                    ) {
                        SettingsGroupCard {
                            Column(modifier = Modifier.padding(16.dp)) {
                                OutlinedTextField(
                                    value = anthropicKey,
                                    onValueChange = { viewModel.saveAnthropicKey(it) },
                                    label = { Text(stringResource(id = R.string.ai_anthropic_key_label)) },
                                    placeholder = { Text("sk-ant-...") },
                                    modifier = Modifier.fillMaxWidth(),
                                    visualTransformation = if (showAnthropicKeyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                    trailingIcon = {
                                        IconButton(onClick = { showAnthropicKeyVisible = !showAnthropicKeyVisible }) {
                                            Icon(
                                                imageVector = if (showAnthropicKeyVisible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                                                contentDescription = null
                                            )
                                        }
                                    },
                                    shape = MaterialTheme.shapes.medium
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = stringResource(id = R.string.ai_anthropic_key_help),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                            ModelSelector(
                                title = stringResource(id = R.string.ai_select_anthropic_model),
                                subtitle = stringResource(id = R.string.ai_model_selector_subtitle),
                                selectedModel = selectedAnthropicModel,
                                availableModels = anthropicModels,
                                onModelSelected = { viewModel.selectAnthropicModel(it) },
                                icon = Icons.Rounded.Psychology,
                                iconColor = Color(0xFFFF5722)
                            )

                        }
                    }
                }
            }

            // Gemini Configuration
            if (selectedProvider == AIProvider.GEMINI) {
                item {
                    ExpressiveSection(
                        title = stringResource(id = R.string.ai_gemini_config_title),
                        description = stringResource(id = R.string.ai_gemini_config_description)
                    ) {
                        SettingsGroupCard {
                            Column(modifier = Modifier.padding(16.dp)) {
                                OutlinedTextField(
                                    value = geminiKey,
                                    onValueChange = { viewModel.saveGeminiKey(it) },
                                    label = { Text(stringResource(id = R.string.ai_gemini_key_label)) },
                                    placeholder = { Text("AIza...") },
                                    modifier = Modifier.fillMaxWidth(),
                                    visualTransformation = if (showGeminiKeyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                    trailingIcon = {
                                        IconButton(onClick = { showGeminiKeyVisible = !showGeminiKeyVisible }) {
                                            Icon(
                                                imageVector = if (showGeminiKeyVisible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                                                contentDescription = null
                                            )
                                        }
                                    },
                                    shape = MaterialTheme.shapes.medium
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = stringResource(id = R.string.ai_gemini_key_help),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                            ModelSelector(
                                title = stringResource(id = R.string.ai_select_gemini_model),
                                subtitle = stringResource(id = R.string.ai_model_selector_subtitle),
                                selectedModel = selectedGeminiModel,
                                availableModels = geminiModels,
                                onModelSelected = { viewModel.selectGeminiModel(it) },
                                icon = Icons.Rounded.ModelTraining,
                                iconColor = Color(0xFF4285F4)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AIProviderCard(
    provider: AIProvider,
    name: String,
    description: String,
    icon: ImageVector,
    iconColor: Color,
    isSelected: Boolean,
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
            androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else {
            null
        }
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

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (isSelected) {
                Icon(
                    Icons.Rounded.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
