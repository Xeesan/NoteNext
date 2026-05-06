"""Extract hardcoded strings from AIUsageDashboardScreen.kt -> stringResource."""

PATH = "app/src/main/java/com/suvojeet/notenext/ui/settings/ai/AIUsageDashboardScreen.kt"

REPLACEMENTS = [
    # Top bar
    (
        '                    Text(\n'
        '                        text = "Usage",\n'
        '                        style = MaterialTheme.typography.headlineLarge,',
        '                    Text(\n'
        '                        text = stringResource(id = R.string.ai_usage_title),\n'
        '                        style = MaterialTheme.typography.headlineLarge,',
    ),
    # Back contentDescription
    (
        'Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")',
        'Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = stringResource(id = R.string.back))',
    ),
    # Clear icon contentDescription
    (
        'Icon(Icons.Rounded.DeleteSweep, contentDescription = "Clear")',
        'Icon(Icons.Rounded.DeleteSweep, contentDescription = stringResource(id = R.string.ai_usage_clear_cd))',
    ),
    # By feature section
    (
        '                    ExpressiveSection(\n'
        '                        title = "By feature",\n'
        '                        description = "How often each AI feature has run on this device"\n'
        '                    ) {',
        '                    ExpressiveSection(\n'
        '                        title = stringResource(id = R.string.ai_usage_section_features),\n'
        '                        description = stringResource(id = R.string.ai_usage_section_features_desc)\n'
        '                    ) {',
    ),
    # Details section
    (
        '                        ExpressiveSection(\n'
        '                            title = "Details",\n'
        '                            description = "Success rate, average latency, acceptance"\n'
        '                        ) {',
        '                        ExpressiveSection(\n'
        '                            title = stringResource(id = R.string.ai_usage_section_details),\n'
        '                            description = stringResource(id = R.string.ai_usage_section_details_desc)\n'
        '                        ) {',
    ),
    # By provider section
    (
        '                        ExpressiveSection(\n'
        '                            title = "By provider",\n'
        '                            description = "Which AI service handled your requests"\n'
        '                        ) {',
        '                        ExpressiveSection(\n'
        '                            title = stringResource(id = R.string.ai_usage_section_provider),\n'
        '                            description = stringResource(id = R.string.ai_usage_section_provider_desc)\n'
        '                        ) {',
    ),
    # Clear dialog
    (
        '            title = { Text("Clear AI usage history?") },\n'
        '            text = { Text("This permanently deletes the local usage stats shown on this dashboard. Your notes and AI feature settings are unaffected.") },',
        '            title = { Text(stringResource(id = R.string.ai_usage_clear_dialog_title)) },\n'
        '            text = { Text(stringResource(id = R.string.ai_usage_clear_dialog_message)) },',
    ),
    # Clear button
    (
        '                    Text("Clear", color = MaterialTheme.colorScheme.error)',
        '                    Text(stringResource(id = R.string.ai_usage_clear_action), color = MaterialTheme.colorScheme.error)',
    ),
    # Cancel button
    (
        '                TextButton(onClick = { showClearConfirm = false }, modifier = Modifier.springPress()) {\n'
        '                    Text("Cancel")\n'
        '                }',
        '                TextButton(onClick = { showClearConfirm = false }, modifier = Modifier.springPress()) {\n'
        '                    Text(stringResource(id = R.string.cancel))\n'
        '                }',
    ),
    # Empty state title
    (
        '            Text(\n'
        '                "No AI activity yet",\n'
        '                style = MaterialTheme.typography.headlineSmall,',
        '            Text(\n'
        '                stringResource(id = R.string.ai_usage_empty_title),\n'
        '                style = MaterialTheme.typography.headlineSmall,',
    ),
    # Empty state subtitle
    (
        '            Text(\n'
        '                "Once you enable an AI feature and use it, you\'ll see how often it ran, how fast it was, and how often you accepted its suggestions — all stored locally on this device.",\n'
        '                style = MaterialTheme.typography.bodyMedium,',
        '            Text(\n'
        '                stringResource(id = R.string.ai_usage_empty_subtitle),\n'
        '                style = MaterialTheme.typography.bodyMedium,',
    ),
    # Hero label
    (
        '            Text(\n'
        '                "AI invocations",\n'
        '                style = MaterialTheme.typography.labelLarge,',
        '            Text(\n'
        '                stringResource(id = R.string.ai_usage_hero_label),\n'
        '                style = MaterialTheme.typography.labelLarge,',
    ),
    # Success pill
    (
        '                StatPill(\n'
        '                    label = "Success",\n'
        '                    value = "$success",',
        '                StatPill(\n'
        '                    label = stringResource(id = R.string.ai_usage_pill_success),\n'
        '                    value = "$success",',
    ),
    # Success rate pill
    (
        '                StatPill(\n'
        '                    label = "Success rate",\n'
        '                    value = "$rate%",',
        '                StatPill(\n'
        '                    label = stringResource(id = R.string.ai_usage_pill_success_rate),\n'
        '                    value = "$rate%",',
    ),
    # Helpfulness label
    (
        '                Text(\n'
        '                    "Suggestions you accepted",\n'
        '                    style = MaterialTheme.typography.labelLarge,',
        '                Text(\n'
        '                    stringResource(id = R.string.ai_usage_helpfulness_label),\n'
        '                    style = MaterialTheme.typography.labelLarge,',
    ),
    # Helpfulness value
    (
        '                Text(\n'
        '                    "${summary.ratePercent}%  ·  ${summary.acceptedCount} of ${summary.suggestionTotal}",\n'
        '                    style = MaterialTheme.typography.titleLarge,',
        '                Text(\n'
        '                    stringResource(id = R.string.ai_usage_helpfulness_value, summary.ratePercent, summary.acceptedCount, summary.suggestionTotal),\n'
        '                    style = MaterialTheme.typography.titleLarge,',
    ),
    # No data
    (
        '        Text("No data yet", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)',
        'Text(stringResource(id = R.string.ai_usage_no_data), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)',
    ),
    # Runs assist chip
    (
        '                label = { Text("${row.total} runs", style = MaterialTheme.typography.labelSmall) }',
        'label = { Text(stringResource(id = R.string.ai_usage_runs, row.total), style = MaterialTheme.typography.labelSmall) }',
    ),
    # FeatureDetailRow stats
    (
        '            DetailStat(label = "Success", value = "$successRate%")\n'
        '            DetailStat(label = "Avg latency", value = avgLatency)\n'
        '            if (feature.isSuggestionFeature) {\n'
        '                DetailStat(label = "Accepted", value = acceptanceText)\n'
        '            }',
        '            DetailStat(label = stringResource(id = R.string.ai_usage_pill_success), value = "$successRate%")\n'
        '            DetailStat(label = stringResource(id = R.string.ai_usage_avg_latency), value = avgLatency)\n'
        '            if (feature.isSuggestionFeature) {\n'
        '                DetailStat(label = stringResource(id = R.string.ai_usage_accepted_label), value = acceptanceText)\n'
        '            }',
    ),
    # FeatureDetailRow acceptance text computation
    (
        '    val acceptanceText = if (row.suggestions > 0) {\n'
        '        val pct = row.accepted * 100 / row.suggestions\n'
        '        "$pct% accepted (${row.accepted}/${row.suggestions})"\n'
        '    } else "—"',
        '    val acceptanceText = if (row.suggestions > 0) {\n'
        '        val pct = row.accepted * 100 / row.suggestions\n'
        '        stringResource(id = R.string.ai_usage_accepted_value, pct, row.accepted, row.suggestions)\n'
        '    } else "—"',
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
        missing.append(old[:80].replace("\n", "\\n"))

if "import androidx.compose.ui.res.stringResource" not in content:
    content = content.replace(
        "import androidx.compose.ui.unit.sp",
        "import androidx.compose.ui.res.stringResource\nimport androidx.compose.ui.unit.sp",
        1,
    )
if "import com.suvojeet.notenext.R" not in content:
    content = content.replace(
        "import com.suvojeet.notenext.data.ai.AIFeature",
        "import com.suvojeet.notenext.R\nimport com.suvojeet.notenext.data.ai.AIFeature",
        1,
    )

with open(PATH, "w", encoding="utf-8", newline="") as f:
    f.write(content)

print(f"Applied: {applied}/{len(REPLACEMENTS)} edits")
if missing:
    print("MISSING:")
    for m in missing:
        print(f"  {m}")
