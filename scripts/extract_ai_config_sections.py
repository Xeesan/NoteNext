"""Extract hardcoded strings from AIProviderConfigSections.kt -> stringResource."""

PATH = "app/src/main/java/com/suvojeet/notenext/ui/settings/ai/AIProviderConfigSections.kt"

REPLACEMENTS = [
    # GroqConfigSection
    (
        '    ExpressiveSection(\n'
        '        title = "Groq configuration",\n'
        '        description = "The app ships with a built-in Groq key. Use your own for higher rate limits."\n'
        '    ) {',
        '    ExpressiveSection(\n'
        '        title = stringResource(id = R.string.ai_config_section_groq),\n'
        '        description = stringResource(id = R.string.ai_config_section_groq_desc)\n'
        '    ) {',
    ),
    # SettingsToggle for use my own Groq key
    (
        '                SettingsToggle(\n'
        '                    icon = Icons.Rounded.VpnKey,\n'
        '                    title = "Use my own Groq key",\n'
        '                    subtitle = if (useCustomKey)\n'
        '                        "Custom key will be sent with every Groq request"\n'
        '                    else\n'
        '                        "Bundled key is used (rate-limited, shared)",',
        '                SettingsToggle(\n'
        '                    icon = Icons.Rounded.VpnKey,\n'
        '                    title = stringResource(id = R.string.ai_config_use_own_groq),\n'
        '                    subtitle = if (useCustomKey)\n'
        '                        stringResource(id = R.string.ai_config_groq_custom_active)\n'
        '                    else\n'
        '                        stringResource(id = R.string.ai_config_groq_bundled_active),',
    ),
    # Groq API key label
    (
        '                        label = { Text("Groq API key") },',
        '                        label = { Text(stringResource(id = R.string.ai_config_groq_key_label)) },',
    ),
    # Groq help text
    (
        '                    Text(\n'
        '                        text = "Stored locally with the rest of your settings. Only sent to api.groq.com.",\n'
        '                        style = MaterialTheme.typography.bodySmall,',
        '                    Text(\n'
        '                        text = stringResource(id = R.string.ai_config_groq_key_help),\n'
        '                        style = MaterialTheme.typography.bodySmall,',
    ),
    # Groq Get key button
    (
        '                            Icon(Icons.Rounded.OpenInNew, contentDescription = null, modifier = Modifier.size(18.dp))\n'
        '                            Spacer(Modifier.width(8.dp))\n'
        '                            Text("Get key")\n'
        '                        }\n'
        '                        Button(\n'
        '                            onClick = onRefreshModels,',
        '                            Icon(Icons.Rounded.OpenInNew, contentDescription = null, modifier = Modifier.size(18.dp))\n'
        '                            Spacer(Modifier.width(8.dp))\n'
        '                            Text(stringResource(id = R.string.ai_config_get_key))\n'
        '                        }\n'
        '                        Button(\n'
        '                            onClick = onRefreshModels,',
    ),
    # Groq Refresh button
    (
        '                            Spacer(Modifier.width(8.dp))\n'
        '                            Text("Refresh")\n'
        '                        }\n'
        '                    }\n'
        '\n'
        '                    if (availableModels.isNotEmpty()) {\n'
        '                        Spacer(Modifier.height(16.dp))\n'
        '                        ModelPicker(\n'
        '                            label = "Fast model (short tasks)",',
        '                            Spacer(Modifier.width(8.dp))\n'
        '                            Text(stringResource(id = R.string.ai_config_refresh))\n'
        '                        }\n'
        '                    }\n'
        '\n'
        '                    if (availableModels.isNotEmpty()) {\n'
        '                        Spacer(Modifier.height(16.dp))\n'
        '                        ModelPicker(\n'
        '                            label = stringResource(id = R.string.ai_config_fast_model),',
    ),
    # Groq Large model picker
    (
        '                        ModelPicker(\n'
        '                            label = "Large model (complex tasks)",',
        '                        ModelPicker(\n'
        '                            label = stringResource(id = R.string.ai_config_large_model),',
    ),
    # OpenAI section
    (
        '    ExpressiveSection(\n'
        '        title = "OpenAI configuration",\n'
        '        description = "Bring your own key. Compatible with self-hosted endpoints via base URL override."\n'
        '    ) {',
        '    ExpressiveSection(\n'
        '        title = stringResource(id = R.string.ai_config_section_openai),\n'
        '        description = stringResource(id = R.string.ai_config_section_openai_desc)\n'
        '    ) {',
    ),
    # OpenAI Base URL label
    (
        '                    label = { Text("Base URL (advanced)") },',
        '                    label = { Text(stringResource(id = R.string.ai_config_openai_base_url)) },',
    ),
    # OpenAI key label
    (
        '                    label = { Text("OpenAI API key") },',
        '                    label = { Text(stringResource(id = R.string.ai_config_openai_key_label)) },',
    ),
    # OpenAI help text
    (
        '                Text(\n'
        '                    text = "Stored locally. Sent only to the URL above.",\n'
        '                    style = MaterialTheme.typography.bodySmall,',
        '                Text(\n'
        '                    text = stringResource(id = R.string.ai_config_openai_key_help),\n'
        '                    style = MaterialTheme.typography.bodySmall,',
    ),
    # OpenAI Get key button
    (
        '                        Icon(Icons.Rounded.OpenInNew, contentDescription = null, modifier = Modifier.size(18.dp))\n'
        '                        Spacer(Modifier.width(8.dp))\n'
        '                        Text("Get key")\n'
        '                    }\n'
        '                    Button(\n'
        '                        onClick = onRefreshModels,',
        '                        Icon(Icons.Rounded.OpenInNew, contentDescription = null, modifier = Modifier.size(18.dp))\n'
        '                        Spacer(Modifier.width(8.dp))\n'
        '                        Text(stringResource(id = R.string.ai_config_get_key))\n'
        '                    }\n'
        '                    Button(\n'
        '                        onClick = onRefreshModels,',
    ),
    # OpenAI Refresh button + Model picker
    (
        '                        Spacer(Modifier.width(8.dp))\n'
        '                        Text("Refresh")\n'
        '                    }\n'
        '                }\n'
        '\n'
        '                if (availableModels.isNotEmpty()) {\n'
        '                    Spacer(Modifier.height(16.dp))\n'
        '                    ModelPicker(\n'
        '                        label = "Model",\n'
        '                        selected = selectedModel,\n'
        '                        options = availableModels,\n'
        '                        onSelect = onSelectModel\n'
        '                    )\n'
        '                }\n'
        '            }\n'
        '        }\n'
        '    }\n'
        '}\n'
        '\n'
        '@Composable\n'
        'internal fun AnthropicConfigSection(',
        '                        Spacer(Modifier.width(8.dp))\n'
        '                        Text(stringResource(id = R.string.ai_config_refresh))\n'
        '                    }\n'
        '                }\n'
        '\n'
        '                if (availableModels.isNotEmpty()) {\n'
        '                    Spacer(Modifier.height(16.dp))\n'
        '                    ModelPicker(\n'
        '                        label = stringResource(id = R.string.ai_config_model),\n'
        '                        selected = selectedModel,\n'
        '                        options = availableModels,\n'
        '                        onSelect = onSelectModel\n'
        '                    )\n'
        '                }\n'
        '            }\n'
        '        }\n'
        '    }\n'
        '}\n'
        '\n'
        '@Composable\n'
        'internal fun AnthropicConfigSection(',
    ),
    # Anthropic section
    (
        '    ExpressiveSection(\n'
        '        title = "Anthropic (Claude) configuration",\n'
        '        description = "Bring your own Anthropic API key. Sent only to api.anthropic.com."\n'
        '    ) {',
        '    ExpressiveSection(\n'
        '        title = stringResource(id = R.string.ai_config_section_anthropic),\n'
        '        description = stringResource(id = R.string.ai_config_section_anthropic_desc)\n'
        '    ) {',
    ),
    # Anthropic key label
    (
        '                    label = { Text("Anthropic API key") },',
        '                    label = { Text(stringResource(id = R.string.ai_config_anthropic_key_label)) },',
    ),
    # Anthropic Get a key + Model picker
    (
        '                    Icon(Icons.Rounded.OpenInNew, contentDescription = null, modifier = Modifier.size(18.dp))\n'
        '                    Spacer(Modifier.width(8.dp))\n'
        '                    Text("Get a key")\n'
        '                }\n'
        '\n'
        '                if (availableModels.isNotEmpty()) {\n'
        '                    Spacer(Modifier.height(16.dp))\n'
        '                    ModelPicker(\n'
        '                        label = "Model",\n'
        '                        selected = selectedModel,\n'
        '                        options = availableModels,\n'
        '                        onSelect = onSelectModel\n'
        '                    )\n'
        '                }\n'
        '            }\n'
        '        }\n'
        '    }\n'
        '}\n'
        '\n'
        '@Composable\n'
        'internal fun GeminiConfigSection(',
        '                    Icon(Icons.Rounded.OpenInNew, contentDescription = null, modifier = Modifier.size(18.dp))\n'
        '                    Spacer(Modifier.width(8.dp))\n'
        '                    Text(stringResource(id = R.string.ai_config_get_a_key))\n'
        '                }\n'
        '\n'
        '                if (availableModels.isNotEmpty()) {\n'
        '                    Spacer(Modifier.height(16.dp))\n'
        '                    ModelPicker(\n'
        '                        label = stringResource(id = R.string.ai_config_model),\n'
        '                        selected = selectedModel,\n'
        '                        options = availableModels,\n'
        '                        onSelect = onSelectModel\n'
        '                    )\n'
        '                }\n'
        '            }\n'
        '        }\n'
        '    }\n'
        '}\n'
        '\n'
        '@Composable\n'
        'internal fun GeminiConfigSection(',
    ),
    # Gemini section
    (
        '    ExpressiveSection(\n'
        '        title = "Google Gemini configuration",\n'
        '        description = "Bring your own Gemini API key. Sent only to generativelanguage.googleapis.com."\n'
        '    ) {',
        '    ExpressiveSection(\n'
        '        title = stringResource(id = R.string.ai_config_section_gemini),\n'
        '        description = stringResource(id = R.string.ai_config_section_gemini_desc)\n'
        '    ) {',
    ),
    # Gemini key label
    (
        '                    label = { Text("Gemini API key") },',
        '                    label = { Text(stringResource(id = R.string.ai_config_gemini_key_label)) },',
    ),
    # Gemini Get a key + Model picker
    (
        '                    Icon(Icons.Rounded.OpenInNew, contentDescription = null, modifier = Modifier.size(18.dp))\n'
        '                    Spacer(Modifier.width(8.dp))\n'
        '                    Text("Get a key")\n'
        '                }\n'
        '\n'
        '                if (availableModels.isNotEmpty()) {\n'
        '                    Spacer(Modifier.height(16.dp))\n'
        '                    ModelPicker(\n'
        '                        label = "Model",\n'
        '                        selected = selectedModel,\n'
        '                        options = availableModels,\n'
        '                        onSelect = onSelectModel\n'
        '                    )\n'
        '                }\n'
        '            }\n'
        '        }\n'
        '    }\n'
        '}\n'
        '\n'
        '@Composable\n'
        'private fun ModelPicker(',
        '                    Icon(Icons.Rounded.OpenInNew, contentDescription = null, modifier = Modifier.size(18.dp))\n'
        '                    Spacer(Modifier.width(8.dp))\n'
        '                    Text(stringResource(id = R.string.ai_config_get_a_key))\n'
        '                }\n'
        '\n'
        '                if (availableModels.isNotEmpty()) {\n'
        '                    Spacer(Modifier.height(16.dp))\n'
        '                    ModelPicker(\n'
        '                        label = stringResource(id = R.string.ai_config_model),\n'
        '                        selected = selectedModel,\n'
        '                        options = availableModels,\n'
        '                        onSelect = onSelectModel\n'
        '                    )\n'
        '                }\n'
        '            }\n'
        '        }\n'
        '    }\n'
        '}\n'
        '\n'
        '@Composable\n'
        'private fun ModelPicker(',
    ),
    # ModelPicker "Select model"
    (
        '                Text(\n'
        '                    selected.ifBlank { "Select model" },',
        '                Text(\n'
        '                    selected.ifBlank { selectModelText },',
    ),
]

content = open(PATH, encoding="utf-8").read()
applied = 0
missing = []
for old, new in REPLACEMENTS:
    if old in content:
        content = content.replace(old, new, 1)
        applied += 1
    else:
        missing.append(old[:60].replace("\n", "\\n"))

# Inject `val selectModelText = stringResource(...)` into ModelPicker
# (it's a @Composable scope so direct stringResource is allowed in Text(...) but
# we need it before the ifBlank lambda since ifBlank's lambda is non-@Composable)
content = content.replace(
    '@Composable\nprivate fun ModelPicker(\n    label: String,\n    selected: String,\n    options: List<String>,\n    onSelect: (String) -> Unit\n) {\n    var expanded by remember { mutableStateOf(false) }',
    '@Composable\nprivate fun ModelPicker(\n    label: String,\n    selected: String,\n    options: List<String>,\n    onSelect: (String) -> Unit\n) {\n    var expanded by remember { mutableStateOf(false) }\n    val selectModelText = stringResource(id = R.string.ai_config_select_model)',
)

# Add stringResource & R imports if missing
if "import androidx.compose.ui.res.stringResource" not in content:
    content = content.replace(
        "import androidx.compose.ui.platform.LocalContext",
        "import androidx.compose.ui.platform.LocalContext\nimport androidx.compose.ui.res.stringResource",
        1,
    )
if "import com.suvojeet.notenext.R" not in content:
    content = content.replace(
        "import com.suvojeet.notenext.ui.components.ExpressiveSection",
        "import com.suvojeet.notenext.R\nimport com.suvojeet.notenext.ui.components.ExpressiveSection",
        1,
    )

with open(PATH, "w", encoding="utf-8", newline="") as f:
    f.write(content)

print(f"Applied: {applied}/{len(REPLACEMENTS)} edits")
if missing:
    print("MISSING patterns:")
    for m in missing:
        print(f"  {m}")
