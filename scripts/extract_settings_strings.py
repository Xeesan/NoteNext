"""Extract hardcoded strings from SettingsScreen.kt -> stringResource / context.getString."""

PATH = "app/src/main/java/com/suvojeet/notenext/ui/settings/SettingsScreen.kt"

# Each tuple: (old_substring, new_substring). Must be unique within the file.
REPLACEMENTS = [
    # --- Section 1: Display section description ---
    (
        'title = context.getString(R.string.display_section_title),\n'
        '                description = "Visuals and language preferences",',
        'title = context.getString(R.string.display_section_title),\n'
        '                description = context.getString(R.string.settings_section_display_subtitle),',
    ),
    # --- Display section: language item subtitle ---
    (
        'subtitle = if (selectedLanguage == "hi") "Hindi (भारत)" else "English (US)",',
        'subtitle = if (selectedLanguage == "hi") context.getString(R.string.settings_lang_subtitle_hi) '
        'else context.getString(R.string.settings_lang_subtitle_en),',
    ),
    # --- Section 2: Data & Maintenance ---
    (
        '            SettingsSectionData(\n'
        '                title = "Data & Maintenance",\n'
        '                description = "Backup, imports and cleanup",',
        '            SettingsSectionData(\n'
        '                title = context.getString(R.string.settings_section_data),\n'
        '                description = context.getString(R.string.settings_section_data_subtitle),',
    ),
    # Auto Cleanup item
    (
        '                        icon = Icons.Rounded.Delete,\n'
        '                        title = "Auto Cleanup",\n'
        '                        subtitle = "Clean bin after $autoDeleteDays days",',
        '                        icon = Icons.Rounded.Delete,\n'
        '                        title = context.getString(R.string.settings_auto_cleanup),\n'
        '                        subtitle = context.getString(R.string.settings_auto_cleanup_subtitle, autoDeleteDays),',
    ),
    # Backup & Restore item
    (
        '                        icon = Icons.Rounded.Backup,\n'
        '                        title = "Backup & Restore",\n'
        '                        subtitle = "Cloud and local data management",',
        '                        icon = Icons.Rounded.Backup,\n'
        '                        title = context.getString(R.string.backup_restore_section_title),\n'
        '                        subtitle = context.getString(R.string.settings_backup_restore_subtitle),',
    ),
    # Privacy & Security item
    (
        '                        icon = Icons.Rounded.Security,\n'
        '                        title = "Privacy & Security",\n'
        '                        subtitle = "App lock, screenshots, clipboard clearing",',
        '                        icon = Icons.Rounded.Security,\n'
        '                        title = context.getString(R.string.settings_privacy_security),\n'
        '                        subtitle = context.getString(R.string.settings_privacy_security_subtitle),',
    ),
    # Import Notes item
    (
        '                        icon = Icons.Rounded.ImportExport,\n'
        '                        title = "Import Notes",\n'
        '                        subtitle = "Import from Google Keep ZIP",',
        '                        icon = Icons.Rounded.ImportExport,\n'
        '                        title = context.getString(R.string.settings_import_notes),\n'
        '                        subtitle = context.getString(R.string.settings_import_notes_subtitle),',
    ),
    # AI item
    (
        '                        icon = Icons.Rounded.AutoAwesome,\n'
        '                        title = "AI",\n'
        '                        subtitle = "Master switch, providers, features, usage dashboard",',
        '                        icon = Icons.Rounded.AutoAwesome,\n'
        '                        title = context.getString(R.string.settings_ai_label),\n'
        '                        subtitle = context.getString(R.string.settings_ai_subtitle),',
    ),
    # --- Section 3: Support & Logging ---
    (
        '            SettingsSectionData(\n'
        '                title = "Support & Logging",\n'
        '                description = "Information and bug reproduction",',
        '            SettingsSectionData(\n'
        '                title = context.getString(R.string.settings_section_support_logging),\n'
        '                description = context.getString(R.string.settings_section_support_logging_subtitle),',
    ),
    # App Info item
    (
        '                        icon = Icons.Rounded.Info,\n'
        '                        title = "App Info",\n'
        '                        subtitle = "v$versionName Stable Build",',
        '                        icon = Icons.Rounded.Info,\n'
        '                        title = context.getString(R.string.settings_app_info),\n'
        '                        subtitle = context.getString(R.string.settings_app_info_subtitle, versionName),',
    ),
    # Logging item title + subtitle
    (
        '                        title = if (isLoggingActive) "Stop Logging" else "Start Logging",\n'
        '                        subtitle = if (isLoggingActive) "Logging active. Reproduce bug now." else "Record app logs to report bugs",',
        '                        title = if (isLoggingActive) context.getString(R.string.settings_stop_logging) '
        'else context.getString(R.string.settings_start_logging),\n'
        '                        subtitle = if (isLoggingActive) context.getString(R.string.settings_logging_active_subtitle) '
        'else context.getString(R.string.settings_start_logging_subtitle),',
    ),
    # Logging toasts
    (
        '                                    Toast.makeText(context, "Log saved and ready to share", Toast.LENGTH_SHORT).show()',
        '                                    Toast.makeText(context, context.getString(R.string.settings_log_saved_toast), Toast.LENGTH_SHORT).show()',
    ),
    (
        '                                Toast.makeText(context, "Logging started. Reproduce the bug now.", Toast.LENGTH_LONG).show()',
        '                                Toast.makeText(context, context.getString(R.string.settings_logging_started_toast), Toast.LENGTH_LONG).show()',
    ),
    # Source Code item
    (
        '                        icon = Icons.Rounded.Code,\n'
        '                        title = "Source Code",\n'
        '                        subtitle = "Check out our GitHub repository",',
        '                        icon = Icons.Rounded.Code,\n'
        '                        title = context.getString(R.string.settings_source_code),\n'
        '                        subtitle = context.getString(R.string.settings_source_code_subtitle),',
    ),
    # --- Top app bar back content description ---
    (
        '                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")',
        '                        Icon(Icons.Rounded.ArrowBack, contentDescription = stringResource(id = com.suvojeet.notenext.core.R.string.back))',
    ),
    # --- Search placeholder ---
    (
        'placeholder = { Text("Search settings...", style = MaterialTheme.typography.bodyLarge) },',
        'placeholder = { Text(stringResource(id = R.string.settings_search_placeholder), style = MaterialTheme.typography.bodyLarge) },',
    ),
    # --- Featured cards ---
    (
        '                        FeaturedCard(\n'
        '                            title = "Privacy",\n'
        '                            subtitle = if (enableAppLock) "Protected" else "Secure now",',
        '                        FeaturedCard(\n'
        '                            title = stringResource(id = R.string.settings_card_privacy),\n'
        '                            subtitle = if (enableAppLock) stringResource(id = R.string.settings_card_protected) '
        'else stringResource(id = R.string.settings_card_secure_now),',
    ),
    (
        '                        FeaturedCard(\n'
        '                            title = "Sync",\n'
        '                            subtitle = "Backup Data",',
        '                        FeaturedCard(\n'
        '                            title = stringResource(id = R.string.settings_card_sync),\n'
        '                            subtitle = stringResource(id = R.string.settings_card_sync_subtitle),',
    ),
    # --- Support & Updates section ---
    (
        '                    ExpressiveSection(\n'
        '                        title = "Support & Updates",\n'
        '                        description = "Keep NoteNext running smoothly"\n'
        '                    ) {',
        '                    ExpressiveSection(\n'
        '                        title = stringResource(id = R.string.settings_section_support_updates),\n'
        '                        description = stringResource(id = R.string.settings_section_support_updates_subtitle)\n'
        '                    ) {',
    ),
    # Rate NoteNext SettingsItem
    (
        '                            SettingsItem(\n'
        '                                icon = Icons.Rounded.Star,\n'
        '                                title = "Rate NoteNext",\n'
        '                                subtitle = "Show some love on Play Store",',
        '                            SettingsItem(\n'
        '                                icon = Icons.Rounded.Star,\n'
        '                                title = stringResource(id = R.string.settings_rate_app),\n'
        '                                subtitle = stringResource(id = R.string.settings_rate_app_subtitle),',
    ),
    # What's New SettingsItem
    (
        '                            SettingsItem(\n'
        '                                icon = Icons.Rounded.NewReleases,\n'
        '                                title = "What\'s New",\n'
        '                                subtitle = "View latest changelog",',
        '                            SettingsItem(\n'
        '                                icon = Icons.Rounded.NewReleases,\n'
        '                                title = stringResource(id = R.string.settings_whats_new),\n'
        '                                subtitle = stringResource(id = R.string.settings_whats_new_subtitle),',
    ),
    # CheckForUpdateItem subtitle
    (
        'subtitle = if (isChecking) stringResource(R.string.checking_for_updates) else "Current: v$currentVersionName",',
        'subtitle = if (isChecking) stringResource(R.string.checking_for_updates) '
        'else stringResource(R.string.settings_current_version, currentVersionName),',
    ),
    # AutoDeleteDialog: "${pos.roundToInt()} days"
    (
        '                Text(\n'
        '                    text = "${pos.roundToInt()} days",\n'
        '                    style = MaterialTheme.typography.displaySmall,',
        '                Text(\n'
        '                    text = stringResource(id = R.string.days, pos.roundToInt()),\n'
        '                    style = MaterialTheme.typography.displaySmall,',
    ),
    # AutoDeleteDialog explanation
    (
        '                Text(\n'
        '                    text = "Notes in trash will be permanently deleted after this period.",\n'
        '                    style = MaterialTheme.typography.bodySmall,',
        '                Text(\n'
        '                    text = stringResource(id = R.string.settings_auto_delete_explanation),\n'
        '                    style = MaterialTheme.typography.bodySmall,',
    ),
    # ImportSourceDialog: title and Cancel
    (
        '        title = { Text("Import from...") },',
        '        title = { Text(stringResource(id = R.string.settings_import_from_title)) },',
    ),
    # ImportSourceDialog Cancel button (line is in this dialog scope)
    (
        '        confirmButton = {},\n'
        '        dismissButton = { \n'
        '            TextButton(onClick = onDismiss, modifier = Modifier.springPress()) { \n'
        '                Text("Cancel") \n'
        '            } \n'
        '        },\n'
        '        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh\n'
        '    )\n'
        '}\n'
        '\n'
        '@Composable\n'
        'fun ImportOptionItem(',
        '        confirmButton = {},\n'
        '        dismissButton = { \n'
        '            TextButton(onClick = onDismiss, modifier = Modifier.springPress()) { \n'
        '                Text(stringResource(id = com.suvojeet.notenext.core.R.string.cancel)) \n'
        '            } \n'
        '        },\n'
        '        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh\n'
        '    )\n'
        '}\n'
        '\n'
        '@Composable\n'
        'fun ImportOptionItem(',
    ),
    # ImportOptionItem "Coming Soon"
    (
        '                Text("Coming Soon", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)',
        'Text(stringResource(id = R.string.settings_coming_soon), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)',
    ),
    # KeepInstructionsDialog
    (
        '        title = { Text("Google Keep Import") },\n'
        '        text = { Text("Please select your Google Takeout ZIP file to import your Keep notes. We\'ll extract your text notes and checklists.") },',
        '        title = { Text(stringResource(id = R.string.settings_keep_import_title)) },\n'
        '        text = { Text(stringResource(id = R.string.settings_keep_import_message)) },',
    ),
    (
        '            Button(onClick = onImport, modifier = Modifier.springPress(), shape = CircleShape) { \n'
        '                Text("Select ZIP") \n'
        '            } \n'
        '        },\n'
        '        dismissButton = { \n'
        '            TextButton(onClick = onDismiss, modifier = Modifier.springPress()) { \n'
        '                Text("Cancel") \n'
        '            } \n'
        '        },\n'
        '        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh\n'
        '    )\n'
        '}\n'
        '\n'
        '@Composable\n'
        'private fun BugReportDialog(',
        '            Button(onClick = onImport, modifier = Modifier.springPress(), shape = CircleShape) { \n'
        '                Text(stringResource(id = R.string.settings_select_zip)) \n'
        '            } \n'
        '        },\n'
        '        dismissButton = { \n'
        '            TextButton(onClick = onDismiss, modifier = Modifier.springPress()) { \n'
        '                Text(stringResource(id = com.suvojeet.notenext.core.R.string.cancel)) \n'
        '            } \n'
        '        },\n'
        '        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh\n'
        '    )\n'
        '}\n'
        '\n'
        '@Composable\n'
        'private fun BugReportDialog(',
    ),
    # BugReportDialog
    (
        '        title = { Text("Bug Report") },\n'
        '        text = { \n'
        '            Column {\n'
        '                Text("Describe the issue you\'re facing. System logs will be attached automatically.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)\n'
        '                Spacer(Modifier.height(16.dp))\n'
        '                OutlinedTextField(\n'
        '                    value = desc, \n'
        '                    onValueChange = onDescChange, \n'
        '                    placeholder = { Text("Issue description...") }, ',
        '        title = { Text(stringResource(id = R.string.settings_bug_report_title)) },\n'
        '        text = { \n'
        '            Column {\n'
        '                Text(stringResource(id = R.string.settings_bug_report_message), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)\n'
        '                Spacer(Modifier.height(16.dp))\n'
        '                OutlinedTextField(\n'
        '                    value = desc, \n'
        '                    onValueChange = onDescChange, \n'
        '                    placeholder = { Text(stringResource(id = R.string.settings_bug_report_placeholder)) }, ',
    ),
    (
        '            ) { \n'
        '                Text("Send Report") \n'
        '            } \n'
        '        },\n'
        '        dismissButton = { \n'
        '            TextButton(onClick = onDismiss, modifier = Modifier.springPress()) { \n'
        '                Text("Cancel") \n'
        '            } \n'
        '        },\n'
        '        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh\n'
        '    )\n'
        '}\n'
        '\n'
        '@Composable\n'
        'fun RateAppDialog(',
        '            ) { \n'
        '                Text(stringResource(id = R.string.settings_send_report)) \n'
        '            } \n'
        '        },\n'
        '        dismissButton = { \n'
        '            TextButton(onClick = onDismiss, modifier = Modifier.springPress()) { \n'
        '                Text(stringResource(id = com.suvojeet.notenext.core.R.string.cancel)) \n'
        '            } \n'
        '        },\n'
        '        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh\n'
        '    )\n'
        '}\n'
        '\n'
        '@Composable\n'
        'fun RateAppDialog(',
    ),
    # RateAppDialog
    (
        '        title = { Text("Rate NoteNext") },\n'
        '        text = { Text("Loving the app? Help us grow by rating it on the Play Store! It only takes a minute.") },',
        '        title = { Text(stringResource(id = R.string.settings_rate_app)) },\n'
        '        text = { Text(stringResource(id = R.string.settings_rate_dialog_message)) },',
    ),
    (
        '            }, modifier = Modifier.springPress(), shape = CircleShape) { \n'
        '                Text("Rate Now") \n'
        '            } \n'
        '        },\n'
        '        dismissButton = { \n'
        '            TextButton(onClick = onDismiss, modifier = Modifier.springPress()) { \n'
        '                Text("Later") \n'
        '            } \n'
        '        },',
        '            }, modifier = Modifier.springPress(), shape = CircleShape) { \n'
        '                Text(stringResource(id = R.string.settings_rate_now)) \n'
        '            } \n'
        '        },\n'
        '        dismissButton = { \n'
        '            TextButton(onClick = onDismiss, modifier = Modifier.springPress()) { \n'
        '                Text(stringResource(id = R.string.settings_later)) \n'
        '            } \n'
        '        },',
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

# Add stringResource import if missing
if "import androidx.compose.ui.res.stringResource" not in content:
    print("ERROR: stringResource import already missing or removed!")

with open(PATH, "w", encoding="utf-8", newline="") as f:
    f.write(content)

print(f"Applied: {applied}/{len(REPLACEMENTS)} edits")
if missing:
    print("MISSING patterns:")
    for m in missing:
        print(f"  {m}")
