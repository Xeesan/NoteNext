"""Extract hardcoded strings from AIProviderSettingsScreen.kt -> stringResource."""
import re

PATH = "app/src/main/java/com/suvojeet/notenext/ui/settings/AIProviderSettingsScreen.kt"

REPLACEMENTS = [
    # Top bar title
    (
        '                    Text(\n'
        '                        text = "AI Providers",\n'
        '                        style = MaterialTheme.typography.headlineLarge,',
        '                    Text(\n'
        '                        text = stringResource(id = R.string.ai_providers_title),\n'
        '                        style = MaterialTheme.typography.headlineLarge,',
    ),
    # Back button
    (
        'Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")',
        'Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = stringResource(id = R.string.back))',
    ),
    # Select AI Provider section
    (
        '                ExpressiveSection(\n'
        '                    title = "Select AI Provider",\n'
        '                    description = "Choose your preferred AI service for summarization, checklists, and more"\n'
        '                ) {',
        '                ExpressiveSection(\n'
        '                    title = stringResource(id = R.string.ai_select_provider_title),\n'
        '                    description = stringResource(id = R.string.ai_select_provider_description)\n'
        '                ) {',
    ),
    # Groq description
    (
        '                                description = "Fast inference with Llama & Qwen models",',
        '                                description = stringResource(id = R.string.ai_provider_groq_desc),',
    ),
    # OpenAI description
    (
        '                                description = "GPT-4o and GPT-4 Turbo models",',
        '                                description = stringResource(id = R.string.ai_provider_openai_desc),',
    ),
    # Anthropic name
    (
        '                                name = "Anthropic (Claude)",\n'
        '                                description = "Claude 3.5 Sonnet and Opus models",',
        '                                name = stringResource(id = R.string.ai_provider_anthropic_name),\n'
        '                                description = stringResource(id = R.string.ai_provider_anthropic_desc),',
    ),
    # Gemini name
    (
        '                                name = "Google Gemini",\n'
        '                                description = "Gemini 3.1 Pro and Flash models",',
        '                                name = stringResource(id = R.string.ai_provider_gemini_name),\n'
        '                                description = stringResource(id = R.string.ai_provider_gemini_desc),',
    ),
    # Groq Configuration section
    (
        '                    ExpressiveSection(\n'
        '                        title = "Groq Configuration",\n'
        '                        description = "Configure your Groq API key (optional)"\n'
        '                    ) {',
        '                    ExpressiveSection(\n'
        '                        title = stringResource(id = R.string.ai_groq_config_title),\n'
        '                        description = stringResource(id = R.string.ai_groq_config_description)\n'
        '                    ) {',
    ),
    # Groq label
    (
        '                                    label = { Text("Groq API Key (Optional)") },',
        '                                    label = { Text(stringResource(id = R.string.ai_groq_key_label)) },',
    ),
    # Groq help text
    (
        '                                Text(\n'
        '                                    text = "Leave empty to use the app\'s built-in key. Get your key at console.groq.com",\n'
        '                                    style = MaterialTheme.typography.bodySmall,',
        '                                Text(\n'
        '                                    text = stringResource(id = R.string.ai_groq_key_help),\n'
        '                                    style = MaterialTheme.typography.bodySmall,',
    ),
    # Advanced Groq Settings button
    (
        '                                    Text("Advanced Groq Settings")',
        '                                    Text(stringResource(id = R.string.ai_groq_advanced_settings))',
    ),
    # OpenAI Configuration section
    (
        '                    ExpressiveSection(\n'
        '                        title = "OpenAI Configuration",\n'
        '                        description = "Enter your OpenAI API key"\n'
        '                    ) {',
        '                    ExpressiveSection(\n'
        '                        title = stringResource(id = R.string.ai_openai_config_title),\n'
        '                        description = stringResource(id = R.string.ai_openai_config_description)\n'
        '                    ) {',
    ),
    # Base URL label
    (
        '                                    label = { Text("Base URL (Optional)") },',
        '                                    label = { Text(stringResource(id = R.string.ai_openai_base_url_label)) },',
    ),
    # OpenAI API Key label
    (
        '                                    label = { Text("OpenAI API Key") },',
        '                                    label = { Text(stringResource(id = R.string.ai_openai_key_label)) },',
    ),
    # OpenAI help text
    (
        '                                Text(\n'
        '                                    text = "Your API key is stored locally and never sent to any server except OpenAI",\n'
        '                                    style = MaterialTheme.typography.bodySmall,',
        '                                Text(\n'
        '                                    text = stringResource(id = R.string.ai_openai_key_help),\n'
        '                                    style = MaterialTheme.typography.bodySmall,',
    ),
    # Fetching/Refresh Models
    (
        '                                            Text("Fetching Models...")',
        '                                            Text(stringResource(id = R.string.ai_fetching_models))',
    ),
    (
        '                                            Text("Refresh Models")',
        '                                            Text(stringResource(id = R.string.ai_refresh_models))',
    ),
    # OpenAI ModelSelector
    (
        '                            ModelSelector(\n'
        '                                title = "Select OpenAI Model",\n'
        '                                subtitle = "Choose which model to use for AI tasks",\n'
        '                                selectedModel = selectedOpenAIModel,',
        '                            ModelSelector(\n'
        '                                title = stringResource(id = R.string.ai_select_openai_model),\n'
        '                                subtitle = stringResource(id = R.string.ai_model_selector_subtitle),\n'
        '                                selectedModel = selectedOpenAIModel,',
    ),
    # Anthropic Configuration section
    (
        '                    ExpressiveSection(\n'
        '                        title = "Anthropic Configuration",\n'
        '                        description = "Enter your Anthropic API key"\n'
        '                    ) {',
        '                    ExpressiveSection(\n'
        '                        title = stringResource(id = R.string.ai_anthropic_config_title),\n'
        '                        description = stringResource(id = R.string.ai_anthropic_config_description)\n'
        '                    ) {',
    ),
    # Anthropic API Key label
    (
        '                                    label = { Text("Anthropic API Key") },',
        '                                    label = { Text(stringResource(id = R.string.ai_anthropic_key_label)) },',
    ),
    # Anthropic help text
    (
        '                                Text(\n'
        '                                    text = "Your API key is stored locally and never sent to any server except Anthropic",\n'
        '                                    style = MaterialTheme.typography.bodySmall,',
        '                                Text(\n'
        '                                    text = stringResource(id = R.string.ai_anthropic_key_help),\n'
        '                                    style = MaterialTheme.typography.bodySmall,',
    ),
    # Anthropic ModelSelector
    (
        '                            ModelSelector(\n'
        '                                title = "Select Anthropic Model",\n'
        '                                subtitle = "Choose which model to use for AI tasks",\n'
        '                                selectedModel = selectedAnthropicModel,',
        '                            ModelSelector(\n'
        '                                title = stringResource(id = R.string.ai_select_anthropic_model),\n'
        '                                subtitle = stringResource(id = R.string.ai_model_selector_subtitle),\n'
        '                                selectedModel = selectedAnthropicModel,',
    ),
    # Gemini Configuration section
    (
        '                    ExpressiveSection(\n'
        '                        title = "Gemini Configuration",\n'
        '                        description = "Enter your Google Gemini API key"\n'
        '                    ) {',
        '                    ExpressiveSection(\n'
        '                        title = stringResource(id = R.string.ai_gemini_config_title),\n'
        '                        description = stringResource(id = R.string.ai_gemini_config_description)\n'
        '                    ) {',
    ),
    # Gemini API Key label
    (
        '                                    label = { Text("Gemini API Key") },',
        '                                    label = { Text(stringResource(id = R.string.ai_gemini_key_label)) },',
    ),
    # Gemini help text
    (
        '                                Text(\n'
        '                                    text = "Your API key is stored locally and never sent to any server except Google",\n'
        '                                    style = MaterialTheme.typography.bodySmall,',
        '                                Text(\n'
        '                                    text = stringResource(id = R.string.ai_gemini_key_help),\n'
        '                                    style = MaterialTheme.typography.bodySmall,',
    ),
    # Gemini ModelSelector
    (
        '                            ModelSelector(\n'
        '                                title = "Select Gemini Model",\n'
        '                                subtitle = "Choose which model to use for AI tasks",\n'
        '                                selectedModel = selectedGeminiModel,',
        '                            ModelSelector(\n'
        '                                title = stringResource(id = R.string.ai_select_gemini_model),\n'
        '                                subtitle = stringResource(id = R.string.ai_model_selector_subtitle),\n'
        '                                selectedModel = selectedGeminiModel,',
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

# Add stringResource & R imports if missing
if "import androidx.compose.ui.res.stringResource" not in content:
    content = content.replace(
        "import androidx.compose.ui.unit.sp",
        "import androidx.compose.ui.res.stringResource\nimport androidx.compose.ui.unit.sp",
        1,
    )
if "import com.suvojeet.notenext.R" not in content:
    content = content.replace(
        "import com.suvojeet.notenext.data.ai.AIProvider",
        "import com.suvojeet.notenext.R\nimport com.suvojeet.notenext.data.ai.AIProvider",
        1,
    )

with open(PATH, "w", encoding="utf-8", newline="") as f:
    f.write(content)

print(f"Applied: {applied}/{len(REPLACEMENTS)} edits")
if missing:
    print("MISSING patterns:")
    for m in missing:
        print(f"  {m}")
