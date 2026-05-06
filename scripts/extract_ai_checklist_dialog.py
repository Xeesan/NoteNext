"""Extract hardcoded strings from AiChecklistDialog.kt -> stringResource."""

PATH = "app/src/main/java/com/suvojeet/notenext/ui/add_edit_note/components/AiChecklistDialog.kt"

REPLACEMENTS = [
    # Toast (uses context.getString since it's not @Composable)
    (
        '                Toast.makeText(context, "No internet connection. Please check your network.", Toast.LENGTH_SHORT).show()',
        '                Toast.makeText(context, context.getString(R.string.ai_checklist_no_internet), Toast.LENGTH_SHORT).show()',
    ),
    # Header
    (
        '                        Text(\n'
        '                            text = "Smart List Creator",\n'
        '                            style = MaterialTheme.typography.headlineSmall,',
        '                        Text(\n'
        '                            text = stringResource(id = R.string.ai_checklist_title),\n'
        '                            style = MaterialTheme.typography.headlineSmall,',
    ),
    (
        '                        Text(\n'
        '                            text = "Powered by AI",\n'
        '                            style = MaterialTheme.typography.labelSmall,',
        '                        Text(\n'
        '                            text = stringResource(id = R.string.ai_checklist_powered_by),\n'
        '                            style = MaterialTheme.typography.labelSmall,',
    ),
    # Close
    (
        '                        Icon(Icons.Outlined.Close, contentDescription = "Close")',
        '                        Icon(Icons.Outlined.Close, contentDescription = stringResource(id = R.string.ai_checklist_close_cd))',
    ),
    # Recent prompts
    (
        '                        Text(\n'
        '                            text = "Recent prompts",\n'
        '                            style = MaterialTheme.typography.labelLarge,',
        '                        Text(\n'
        '                            text = stringResource(id = R.string.ai_checklist_recent_prompts),\n'
        '                            style = MaterialTheme.typography.labelLarge,',
    ),
    # Prompt label
    (
        '                        Text(\n'
        '                            text = "What list should I create?",\n'
        '                            style = MaterialTheme.typography.labelMedium,',
        '                        Text(\n'
        '                            text = stringResource(id = R.string.ai_checklist_prompt_label),\n'
        '                            style = MaterialTheme.typography.labelMedium,',
    ),
    # Placeholder text
    (
        '                                        Text(\n'
        '                                            text = "e.g. Packing list for hiking trip",\n'
        '                                            style = MaterialTheme.typography.bodyLarge,',
        '                                        Text(\n'
        '                                            text = stringResource(id = R.string.ai_checklist_prompt_placeholder),\n'
        '                                            style = MaterialTheme.typography.bodyLarge,',
    ),
    # Regenerate
    (
        '                                    Icon(\n'
        '                                        Icons.Outlined.Refresh,\n'
        '                                        contentDescription = "Regenerate",',
        '                                    Icon(\n'
        '                                        Icons.Outlined.Refresh,\n'
        '                                        contentDescription = stringResource(id = R.string.ai_checklist_regenerate_cd),',
    ),
    # Thinking
    (
        '                                        Text(\n'
        '                                            "Thinking...",\n'
        '                                            style = MaterialTheme.typography.titleMedium,',
        '                                        Text(\n'
        '                                            stringResource(id = R.string.ai_checklist_thinking),\n'
        '                                            style = MaterialTheme.typography.titleMedium,',
    ),
    # Proposed Items
    (
        '                                    Text(\n'
        '                                        text = "Proposed Items",\n'
        '                                        style = MaterialTheme.typography.titleMedium,',
        '                                    Text(\n'
        '                                        text = stringResource(id = R.string.ai_checklist_proposed_items),\n'
        '                                        style = MaterialTheme.typography.titleMedium,',
    ),
    # Item count
    (
        '                                    Text(\n'
        '                                        text = "${editableItems.size} items",\n'
        '                                        style = MaterialTheme.typography.labelMedium,',
        '                                    Text(\n'
        '                                        text = stringResource(id = R.string.ai_checklist_items_count, editableItems.size),\n'
        '                                        style = MaterialTheme.typography.labelMedium,',
    ),
    # Disclaimer
    (
        '                                        Text(\n'
        '                                            text = "AI might provide inaccurate info. Please verify.",\n'
        '                                            style = MaterialTheme.typography.bodySmall,',
        '                                        Text(\n'
        '                                            text = stringResource(id = R.string.ai_checklist_disclaimer),\n'
        '                                            style = MaterialTheme.typography.bodySmall,',
    ),
    # Generate List button
    (
        '                            Text(\n'
        '                                "Generate List",\n'
        '                                style = MaterialTheme.typography.titleMedium,',
        '                            Text(\n'
        '                                stringResource(id = R.string.ai_checklist_generate),\n'
        '                                style = MaterialTheme.typography.titleMedium,',
    ),
    # Insert into Note button
    (
        '                            Text(\n'
        '                                "Insert into Note",\n'
        '                                style = MaterialTheme.typography.titleMedium,',
        '                            Text(\n'
        '                                stringResource(id = R.string.ai_checklist_insert),\n'
        '                                style = MaterialTheme.typography.titleMedium,',
    ),
    # Remove item content desc
    (
        '                    Icon(\n'
        '                        Icons.Default.Delete,\n'
        '                        contentDescription = "Remove item",',
        '                    Icon(\n'
        '                        Icons.Default.Delete,\n'
        '                        contentDescription = stringResource(id = R.string.ai_checklist_remove_item_cd),',
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

if "import androidx.compose.ui.res.stringResource" not in content:
    content = content.replace(
        "import androidx.compose.ui.platform.LocalContext",
        "import androidx.compose.ui.platform.LocalContext\nimport androidx.compose.ui.res.stringResource",
        1,
    )
if "import com.suvojeet.notenext.R" not in content:
    content = content.replace(
        "import com.suvojeet.notenext.ui.components.springPress",
        "import com.suvojeet.notenext.R\nimport com.suvojeet.notenext.ui.components.springPress",
        1,
    )

with open(PATH, "w", encoding="utf-8", newline="") as f:
    f.write(content)

print(f"Applied: {applied}/{len(REPLACEMENTS)} edits")
if missing:
    print("MISSING:")
    for m in missing:
        print(f"  {m}")
