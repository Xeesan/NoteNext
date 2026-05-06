@file:OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
package com.suvojeet.notenext.ui.settings.ai

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.suvojeet.notenext.R
import com.suvojeet.notenext.ui.components.ExpressiveSection
import com.suvojeet.notenext.ui.components.SettingsGroupCard
import com.suvojeet.notenext.ui.components.springPress

@Composable
internal fun GroqConfigSection(
    useCustomKey: Boolean,
    customKey: String,
    availableModels: List<String>,
    fastModel: String,
    largeModel: String,
    onSetUseCustomKey: (Boolean) -> Unit,
    onSaveKey: (String) -> Unit,
    onRefreshModels: () -> Unit,
    isRefreshing: Boolean,
    onSetFastModel: (String) -> Unit,
    onSetLargeModel: (String) -> Unit
) {
    val context = LocalContext.current
    var keyVisible by remember { mutableStateOf(false) }

    ExpressiveSection(
        title = stringResource(id = R.string.ai_config_section_groq),
        description = stringResource(id = R.string.ai_config_section_groq_desc)
    ) {
        SettingsGroupCard {
            Column(modifier = Modifier.padding(16.dp)) {
                SettingsToggle(
                    icon = Icons.Rounded.VpnKey,
                    title = stringResource(id = R.string.ai_config_use_own_groq),
                    subtitle = if (useCustomKey)
                        stringResource(id = R.string.ai_config_groq_custom_active)
                    else
                        stringResource(id = R.string.ai_config_groq_bundled_active),
                    iconColor = MaterialTheme.colorScheme.primary,
                    checked = useCustomKey,
                    onCheckedChange = onSetUseCustomKey
                )

                if (useCustomKey) {
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = customKey,
                        onValueChange = onSaveKey,
                        label = { Text(stringResource(id = R.string.ai_config_groq_key_label)) },
                        placeholder = { Text("gsk_...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (keyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { keyVisible = !keyVisible }) {
                                Icon(
                                    if (keyVisible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                                    contentDescription = null
                                )
                            }
                        },
                        shape = MaterialTheme.shapes.medium
                    )

                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = stringResource(id = R.string.ai_config_groq_key_help),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = {
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://console.groq.com/keys")))
                            },
                            modifier = Modifier.weight(1f).springPress(),
                            shape = MaterialTheme.shapes.large
                        ) {
                            Icon(Icons.Rounded.OpenInNew, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(id = R.string.ai_config_get_key))
                        }
                        Button(
                            onClick = onRefreshModels,
                            enabled = !isRefreshing && customKey.isNotBlank(),
                            modifier = Modifier.weight(1f).springPress(),
                            shape = MaterialTheme.shapes.large
                        ) {
                            if (isRefreshing) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Rounded.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                            }
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(id = R.string.ai_config_refresh))
                        }
                    }

                    if (availableModels.isNotEmpty()) {
                        Spacer(Modifier.height(16.dp))
                        ModelPicker(
                            label = stringResource(id = R.string.ai_config_fast_model),
                            selected = fastModel,
                            options = availableModels,
                            onSelect = onSetFastModel
                        )
                        Spacer(Modifier.height(8.dp))
                        ModelPicker(
                            label = stringResource(id = R.string.ai_config_large_model),
                            selected = largeModel,
                            options = availableModels,
                            onSelect = onSetLargeModel
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun OpenAIConfigSection(
    apiKey: String,
    baseUrl: String,
    selectedModel: String,
    availableModels: List<String>,
    onSaveKey: (String) -> Unit,
    onSaveBaseUrl: (String) -> Unit,
    onSelectModel: (String) -> Unit,
    onRefreshModels: () -> Unit,
    isRefreshing: Boolean
) {
    val context = LocalContext.current
    var keyVisible by remember { mutableStateOf(false) }

    ExpressiveSection(
        title = stringResource(id = R.string.ai_config_section_openai),
        description = stringResource(id = R.string.ai_config_section_openai_desc)
    ) {
        SettingsGroupCard {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = baseUrl,
                    onValueChange = onSaveBaseUrl,
                    label = { Text(stringResource(id = R.string.ai_config_openai_base_url)) },
                    placeholder = { Text("https://api.openai.com/") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = apiKey,
                    onValueChange = onSaveKey,
                    label = { Text(stringResource(id = R.string.ai_config_openai_key_label)) },
                    placeholder = { Text("sk-...") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (keyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { keyVisible = !keyVisible }) {
                            Icon(
                                if (keyVisible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                                contentDescription = null
                            )
                        }
                    },
                    shape = MaterialTheme.shapes.medium
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(id = R.string.ai_config_openai_key_help),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://platform.openai.com/api-keys")))
                        },
                        modifier = Modifier.weight(1f).springPress(),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Icon(Icons.Rounded.OpenInNew, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(id = R.string.ai_config_get_key))
                    }
                    Button(
                        onClick = onRefreshModels,
                        enabled = !isRefreshing && apiKey.isNotBlank(),
                        modifier = Modifier.weight(1f).springPress(),
                        shape = MaterialTheme.shapes.large
                    ) {
                        if (isRefreshing) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Rounded.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(id = R.string.ai_config_refresh))
                    }
                }

                if (availableModels.isNotEmpty()) {
                    Spacer(Modifier.height(16.dp))
                    ModelPicker(
                        label = stringResource(id = R.string.ai_config_model),
                        selected = selectedModel,
                        options = availableModels,
                        onSelect = onSelectModel
                    )
                }
            }
        }
    }
}

@Composable
internal fun AnthropicConfigSection(
    apiKey: String,
    selectedModel: String,
    availableModels: List<String>,
    onSaveKey: (String) -> Unit,
    onSelectModel: (String) -> Unit
) {
    val context = LocalContext.current
    var keyVisible by remember { mutableStateOf(false) }

    ExpressiveSection(
        title = stringResource(id = R.string.ai_config_section_anthropic),
        description = stringResource(id = R.string.ai_config_section_anthropic_desc)
    ) {
        SettingsGroupCard {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = apiKey,
                    onValueChange = onSaveKey,
                    label = { Text(stringResource(id = R.string.ai_config_anthropic_key_label)) },
                    placeholder = { Text("sk-ant-...") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (keyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { keyVisible = !keyVisible }) {
                            Icon(
                                if (keyVisible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                                contentDescription = null
                            )
                        }
                    },
                    shape = MaterialTheme.shapes.medium
                )

                Spacer(Modifier.height(12.dp))
                OutlinedButton(
                    onClick = {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://console.anthropic.com/settings/keys")))
                    },
                    modifier = Modifier.fillMaxWidth().springPress(),
                    shape = MaterialTheme.shapes.large
                ) {
                    Icon(Icons.Rounded.OpenInNew, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(id = R.string.ai_config_get_a_key))
                }

                if (availableModels.isNotEmpty()) {
                    Spacer(Modifier.height(16.dp))
                    ModelPicker(
                        label = stringResource(id = R.string.ai_config_model),
                        selected = selectedModel,
                        options = availableModels,
                        onSelect = onSelectModel
                    )
                }
            }
        }
    }
}

@Composable
internal fun GeminiConfigSection(
    apiKey: String,
    selectedModel: String,
    availableModels: List<String>,
    onSaveKey: (String) -> Unit,
    onSelectModel: (String) -> Unit
) {
    val context = LocalContext.current
    var keyVisible by remember { mutableStateOf(false) }

    ExpressiveSection(
        title = stringResource(id = R.string.ai_config_section_gemini),
        description = stringResource(id = R.string.ai_config_section_gemini_desc)
    ) {
        SettingsGroupCard {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = apiKey,
                    onValueChange = onSaveKey,
                    label = { Text(stringResource(id = R.string.ai_config_gemini_key_label)) },
                    placeholder = { Text("AIza...") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (keyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { keyVisible = !keyVisible }) {
                            Icon(
                                if (keyVisible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                                contentDescription = null
                            )
                        }
                    },
                    shape = MaterialTheme.shapes.medium
                )

                Spacer(Modifier.height(12.dp))
                OutlinedButton(
                    onClick = {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://aistudio.google.com/app/apikey")))
                    },
                    modifier = Modifier.fillMaxWidth().springPress(),
                    shape = MaterialTheme.shapes.large
                ) {
                    Icon(Icons.Rounded.OpenInNew, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(id = R.string.ai_config_get_a_key))
                }

                if (availableModels.isNotEmpty()) {
                    Spacer(Modifier.height(16.dp))
                    ModelPicker(
                        label = stringResource(id = R.string.ai_config_model),
                        selected = selectedModel,
                        options = availableModels,
                        onSelect = onSelectModel
                    )
                }
            }
        }
    }
}

@Composable
private fun ModelPicker(
    label: String,
    selected: String,
    options: List<String>,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectModelText = stringResource(id = R.string.ai_config_select_model)

    Column {
        Text(label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        OutlinedCard(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth().springPress(),
            shape = MaterialTheme.shapes.medium
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    selected.ifBlank { selectModelText },
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyLarge
                )
                Icon(Icons.Rounded.ArrowDropDown, contentDescription = null)
            }
        }
    }

    if (expanded) {
        ModalBottomSheet(onDismissRequest = { expanded = false }) {
            LazyColumn(modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)) {
                items(options) { option ->
                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelect(option)
                                expanded = false
                            },
                        headlineContent = { Text(option) },
                        trailingContent = if (option == selected) {
                            { Icon(Icons.Rounded.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
                        } else null,
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )
                }
            }
        }
    }
}
