"""Extract hardcoded strings from PrivacySecurityScreen.kt -> stringResource."""

PATH = "app/src/main/java/com/suvojeet/notenext/ui/settings/PrivacySecurityScreen.kt"

REPLACEMENTS = [
    # Top bar title
    (
        '                    Text(\n'
        '                        text = "Privacy & Security",\n'
        '                        style = MaterialTheme.typography.headlineLarge,',
        '                    Text(\n'
        '                        text = stringResource(id = R.string.settings_privacy_security),\n'
        '                        style = MaterialTheme.typography.headlineLarge,',
    ),
    # Back
    (
        'Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")',
        'Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = stringResource(id = R.string.back))',
    ),
    # App Access section
    (
        '                ExpressiveSection(\n'
        '                    title = "App Access",\n'
        '                    description = "Control how you and others access your notes"\n'
        '                ) {',
        '                ExpressiveSection(\n'
        '                    title = stringResource(id = R.string.priv_section_app_access),\n'
        '                    description = stringResource(id = R.string.priv_section_app_access_desc)\n'
        '                ) {',
    ),
    # Toast: enroll fallback
    (
        '                                                android.widget.Toast.makeText(context, "Please enable a screen lock or biometrics in system settings.", android.widget.Toast.LENGTH_LONG).show()',
        '                                                android.widget.Toast.makeText(context, context.getString(R.string.priv_toast_enroll_fallback), android.widget.Toast.LENGTH_LONG).show()',
    ),
    # Toast: biometric unavailable
    (
        '                                            android.widget.Toast.makeText(context, "Biometric authentication is not available on this device.", android.widget.Toast.LENGTH_LONG).show()',
        '                                            android.widget.Toast.makeText(context, context.getString(R.string.priv_toast_biometric_unavailable), android.widget.Toast.LENGTH_LONG).show()',
    ),
    # Decoy Vault section
    (
        '                ExpressiveSection(\n'
        '                    title = "Decoy Vault",\n'
        '                    description = "A secondary PIN that reveals a fake set of notes"\n'
        '                ) {',
        '                ExpressiveSection(\n'
        '                    title = stringResource(id = R.string.priv_section_decoy),\n'
        '                    description = stringResource(id = R.string.priv_section_decoy_desc)\n'
        '                ) {',
    ),
    # Enable Decoy Vault toggle
    (
        '                        SettingsToggle(\n'
        '                            icon = Icons.Rounded.VisibilityOff,\n'
        '                            title = "Enable Decoy Vault",\n'
        '                            subtitle = "Use a secondary PIN for coercion situations",',
        '                        SettingsToggle(\n'
        '                            icon = Icons.Rounded.VisibilityOff,\n'
        '                            title = stringResource(id = R.string.priv_enable_decoy),\n'
        '                            subtitle = stringResource(id = R.string.priv_enable_decoy_subtitle),',
    ),
    # Set Decoy PIN item
    (
        '                            SettingsItem(\n'
        '                                icon = Icons.Rounded.Password,\n'
        '                                title = "Set Decoy PIN",\n'
        '                                subtitle = if (!decoyPinSet) "Not set" else "****",',
        '                            SettingsItem(\n'
        '                                icon = Icons.Rounded.Password,\n'
        '                                title = stringResource(id = R.string.priv_set_decoy_pin),\n'
        '                                subtitle = if (!decoyPinSet) stringResource(id = R.string.priv_decoy_pin_not_set) '
        'else stringResource(id = R.string.priv_decoy_pin_masked),',
    ),
    # Data Privacy section
    (
        '                ExpressiveSection(\n'
        '                    title = "Data Privacy",\n'
        '                    description = "Protect your note content from outside eyes"\n'
        '                ) {',
        '                ExpressiveSection(\n'
        '                    title = stringResource(id = R.string.priv_section_data),\n'
        '                    description = stringResource(id = R.string.priv_section_data_desc)\n'
        '                ) {',
    ),
    # Disallow Screenshots toggle
    (
        '                        SettingsToggle(\n'
        '                            icon = Icons.Rounded.Lock,\n'
        '                            title = "Disallow Screenshots",\n'
        '                            subtitle = "Prevent screen capture and hide content in recents",',
        '                        SettingsToggle(\n'
        '                            icon = Icons.Rounded.Lock,\n'
        '                            title = stringResource(id = R.string.priv_disallow_screenshots),\n'
        '                            subtitle = stringResource(id = R.string.priv_disallow_screenshots_subtitle),',
    ),
    # Clipboard item
    (
        '                        SettingsItem(\n'
        '                            icon = Icons.Rounded.ContentPasteOff,\n'
        '                            title = "Smart Clipboard Clear",\n'
        '                            subtitle = when(clipboardTimeout) {\n'
        '                                0L -> "Disabled"\n'
        '                                1L -> "Immediately on background"\n'
        '                                30_000L -> "After 30 seconds"\n'
        '                                60_000L -> "After 1 minute"\n'
        '                                300_000L -> "After 5 minutes"\n'
        '                                else -> "Custom timeout"\n'
        '                            },',
        '                        SettingsItem(\n'
        '                            icon = Icons.Rounded.ContentPasteOff,\n'
        '                            title = stringResource(id = R.string.priv_clipboard_clear_title),\n'
        '                            subtitle = when(clipboardTimeout) {\n'
        '                                0L -> stringResource(id = R.string.priv_clipboard_disabled)\n'
        '                                1L -> stringResource(id = R.string.priv_clipboard_immediately_bg)\n'
        '                                30_000L -> stringResource(id = R.string.priv_clipboard_30s)\n'
        '                                60_000L -> stringResource(id = R.string.priv_clipboard_1m)\n'
        '                                300_000L -> stringResource(id = R.string.priv_clipboard_5m)\n'
        '                                else -> stringResource(id = R.string.priv_clipboard_custom)\n'
        '                            },',
    ),
    # Ephemeral section
    (
        '                ExpressiveSection(\n'
        '                    title = "Ephemeral Content",\n'
        '                    description = "Notes that don\'t stick around"\n'
        '                ) {',
        '                ExpressiveSection(\n'
        '                    title = stringResource(id = R.string.priv_section_ephemeral),\n'
        '                    description = stringResource(id = R.string.priv_section_ephemeral_desc)\n'
        '                ) {',
    ),
    # Self-Destructing item
    (
        '                        SettingsItem(\n'
        '                            icon = Icons.Rounded.Timer,\n'
        '                            title = "Self-Destructing Notes",\n'
        '                            subtitle = "Set notes to automatically delete after a certain time",',
        '                        SettingsItem(\n'
        '                            icon = Icons.Rounded.Timer,\n'
        '                            title = stringResource(id = R.string.priv_self_destruct_title),\n'
        '                            subtitle = stringResource(id = R.string.priv_self_destruct_subtitle),',
    ),
    # Self-destruct dialog
    (
        '            title = { Text("Self-Destructing Notes") },\n'
        '            text = {\n'
        '                Text("Self-destruct is a per-note feature. You can set a timer for any note by clicking the \'More\' (three-dots) menu while editing a note and selecting \'Self-Destruct Timer\'. Once the timer expires, the note will be permanently deleted.")\n'
        '            },',
        '            title = { Text(stringResource(id = R.string.priv_self_destruct_title)) },\n'
        '            text = {\n'
        '                Text(stringResource(id = R.string.priv_self_destruct_message))\n'
        '            },',
    ),
    # Got it
    (
        '                TextButton(onClick = { showSelfDestructInfo = false }) {\n'
        '                    Text("Got it")\n'
        '                }',
        '                TextButton(onClick = { showSelfDestructInfo = false }) {\n'
        '                    Text(stringResource(id = R.string.backup_encryption_info_got_it))\n'
        '                }',
    ),
    # ClipboardTimeoutDialog options list
    (
        '    val options = listOf(\n'
        '        0L to "Disabled",\n'
        '        1L to "Immediately",\n'
        '        30_000L to "30 Seconds",\n'
        '        60_000L to "1 Minute",\n'
        '        300_000L to "5 Minutes"\n'
        '    )',
        '    val options = listOf(\n'
        '        0L to stringResource(id = R.string.priv_clipboard_opt_disabled),\n'
        '        1L to stringResource(id = R.string.priv_clipboard_opt_immediate),\n'
        '        30_000L to stringResource(id = R.string.priv_clipboard_opt_30s),\n'
        '        60_000L to stringResource(id = R.string.priv_clipboard_opt_1m),\n'
        '        300_000L to stringResource(id = R.string.priv_clipboard_opt_5m)\n'
        '    )',
    ),
    # ClipboardTimeoutDialog title
    (
        '        title = { Text("Clipboard Clear Timeout") },',
        '        title = { Text(stringResource(id = R.string.priv_clipboard_dialog_title)) },',
    ),
    # ClipboardTimeoutDialog Cancel
    (
        '            TextButton(onClick = onDismiss) {\n'
        '                Text("Cancel")\n'
        '            }\n'
        '        }\n'
        '    )\n'
        '}\n'
        '\n'
        '@Composable\n'
        'private fun SettingsToggle(',
        '            TextButton(onClick = onDismiss) {\n'
        '                Text(stringResource(id = R.string.cancel))\n'
        '            }\n'
        '        }\n'
        '    )\n'
        '}\n'
        '\n'
        '@Composable\n'
        'private fun SettingsToggle(',
    ),
    # DecoyPinDialog title
    (
        '        title = { Text("Set Decoy PIN") },',
        '        title = { Text(stringResource(id = R.string.priv_decoy_dialog_title)) },',
    ),
    # DecoyPinDialog message
    (
        '                Text("Enter a 4-digit secondary PIN. Entering this PIN on the lock screen will open a separate, decoy vault.")',
        'Text(stringResource(id = R.string.priv_decoy_dialog_message))',
    ),
    # PIN labels
    (
        '                    label = { Text("Secondary PIN") },',
        '                    label = { Text(stringResource(id = R.string.priv_decoy_pin_label)) },',
    ),
    (
        '                    label = { Text("Confirm Secondary PIN") },',
        '                    label = { Text(stringResource(id = R.string.priv_decoy_pin_confirm_label)) },',
    ),
    # PIN errors
    (
        '                        pin.length != 4 -> error = "PIN must be 4 digits"\n'
        '                        pin != confirmPin -> error = "PINs do not match"',
        '                        pin.length != 4 -> error = errPinLength\n'
        '                        pin != confirmPin -> error = errPinMismatch',
    ),
    (
        '                                    error = "Decoy PIN must differ from your app PIN"',
        '                                    error = errPinClash',
    ),
    # Save / Saving / Cancel buttons in DecoyPinDialog
    (
        '                Text(if (checking) "Saving…" else "Save")',
        'Text(if (checking) stringResource(id = R.string.priv_saving) else stringResource(id = R.string.save))',
    ),
    (
        '            TextButton(onClick = onDismiss) {\n'
        '                Text("Cancel")\n'
        '            }\n'
        '        },\n'
        '        shape = MaterialTheme.shapes.extraLarge\n'
        '    )\n'
        '}\n'
        '\n'
        '@Composable\n'
        'private fun SettingsItem(',
        '            TextButton(onClick = onDismiss) {\n'
        '                Text(stringResource(id = R.string.cancel))\n'
        '            }\n'
        '        },\n'
        '        shape = MaterialTheme.shapes.extraLarge\n'
        '    )\n'
        '}\n'
        '\n'
        '@Composable\n'
        'private fun SettingsItem(',
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

# Inject pre-resolved error strings into DecoyPinDialog (after `var checking by remember`)
content = content.replace(
    '    var checking by remember { mutableStateOf(false) }\n'
    '    val dialogScope = rememberCoroutineScope()',
    '    var checking by remember { mutableStateOf(false) }\n'
    '    val dialogScope = rememberCoroutineScope()\n'
    '    val errPinLength = stringResource(id = R.string.priv_err_pin_length)\n'
    '    val errPinMismatch = stringResource(id = R.string.priv_err_pin_mismatch)\n'
    '    val errPinClash = stringResource(id = R.string.priv_err_pin_clash)',
)

if "import androidx.compose.ui.res.stringResource" not in content:
    content = content.replace(
        "import androidx.compose.ui.platform.LocalContext",
        "import androidx.compose.ui.platform.LocalContext\nimport androidx.compose.ui.res.stringResource",
        1,
    )

with open(PATH, "w", encoding="utf-8", newline="") as f:
    f.write(content)

print(f"Applied: {applied}/{len(REPLACEMENTS)} edits")
if missing:
    print("MISSING:")
    for m in missing:
        print(f"  {m}")
