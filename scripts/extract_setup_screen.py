"""Extract hardcoded strings from SetupScreen.kt -> stringResource."""

PATH = "app/src/main/java/com/suvojeet/notenext/ui/setup/SetupScreen.kt"

REPLACEMENTS = [
    # Back button
    (
        '                                        Spacer(Modifier.width(8.dp))\n'
        '                                        Text("Back")',
        '                                        Spacer(Modifier.width(8.dp))\n'
        '                                        Text(stringResource(id = R.string.setup_back))',
    ),
    # Next/Get Started
    (
        '                                    Text(\n'
        '                                        text = if (pagerState.currentPage == 2) "Get Started" else "Next",\n'
        '                                        fontWeight = FontWeight.Bold,',
        '                                    Text(\n'
        '                                        text = if (pagerState.currentPage == 2) stringResource(id = R.string.setup_get_started) '
        'else stringResource(id = R.string.setup_next),\n'
        '                                        fontWeight = FontWeight.Bold,',
    ),
    # Welcome title
    (
        '            Text(\n'
        '                text = "Welcome to NoteNext",\n'
        '                style = MaterialTheme.typography.displaySmall,',
        '            Text(\n'
        '                text = stringResource(id = R.string.setup_welcome_title),\n'
        '                style = MaterialTheme.typography.displaySmall,',
    ),
    # Welcome subtitle
    (
        '            Text(\n'
        '                text = "Your secure, expressive, and local notepad designed for everyone.",\n'
        '                style = MaterialTheme.typography.bodyLarge,',
        '            Text(\n'
        '                text = stringResource(id = R.string.setup_welcome_subtitle),\n'
        '                style = MaterialTheme.typography.bodyLarge,',
    ),
    # Feature list
    (
        '            val features = listOf(\n'
        '                "🔒 Private & Local" to 600,\n'
        '                "☁️ Cloud Backup" to 700,\n'
        '                "🔔 Smart Reminders" to 800\n'
        '            )',
        '            val features = listOf(\n'
        '                stringResource(id = R.string.setup_feature_private) to 600,\n'
        '                stringResource(id = R.string.setup_feature_cloud) to 700,\n'
        '                stringResource(id = R.string.setup_feature_reminders) to 800\n'
        '            )',
    ),
    # Privacy consent
    (
        '            androidx.compose.material3.Text(\n'
        '                text = "By continuing, you agree to our Privacy Policy",\n'
        '                style = MaterialTheme.typography.labelSmall,',
        '            androidx.compose.material3.Text(\n'
        '                text = stringResource(id = R.string.setup_privacy_consent),\n'
        '                style = MaterialTheme.typography.labelSmall,',
    ),
    # Cloud Sync title
    (
        '        Text(\n'
        '            text = "Cloud Sync",\n'
        '            style = MaterialTheme.typography.headlineMedium,',
        '        Text(\n'
        '            text = stringResource(id = R.string.setup_cloud_sync_title),\n'
        '            style = MaterialTheme.typography.headlineMedium,',
    ),
    # Cloud Sync subtitle
    (
        '        Text(\n'
        '            text = "Secure Google Drive Backup",\n'
        '            style = MaterialTheme.typography.bodyMedium,',
        '        Text(\n'
        '            text = stringResource(id = R.string.setup_cloud_sync_subtitle),\n'
        '            style = MaterialTheme.typography.bodyMedium,',
    ),
    # Connected/Pitch text
    (
        '                Text(\n'
        '                    text = if(backupState.googleAccountEmail != null) "Connected to ${backupState.googleAccountEmail}" else "Keep your notes synced across devices with secure cloud storage.",\n'
        '                    style = MaterialTheme.typography.bodyMedium,',
        '                Text(\n'
        '                    text = if(backupState.googleAccountEmail != null) stringResource(id = R.string.setup_connected_to, backupState.googleAccountEmail!!) '
        'else stringResource(id = R.string.setup_cloud_sync_pitch),\n'
        '                    style = MaterialTheme.typography.bodyMedium,',
    ),
    # Connect Google Account button
    (
        '                        Text("Connect Google Account", fontWeight = FontWeight.Bold)',
        '                        Text(stringResource(id = R.string.setup_connect_google), fontWeight = FontWeight.Bold)',
    ),
    # Daily Auto Backup
    (
        '                                    Text("Daily Auto Backup", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)\n'
        '                                    Text("Highly recommended", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)',
        '                                    Text(stringResource(id = R.string.setup_daily_auto_backup), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)\n'
        '                                    Text(stringResource(id = R.string.setup_highly_recommended), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)',
    ),
    # Data Restored
    (
        '                                    Text("Data Restored Successfully", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)',
        '                                    Text(stringResource(id = R.string.setup_data_restored), style = MaterialTheme.typography.bodyMedium, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)',
    ),
    # Restore Previous Notes
    (
        '                                Text("Restore Previous Notes", fontWeight = FontWeight.Bold)',
        '                                Text(stringResource(id = R.string.setup_restore_previous), fontWeight = FontWeight.Bold)',
    ),
    # System Access title
    (
        '        Text(\n'
        '            text = "System Access",\n'
        '            style = MaterialTheme.typography.headlineMedium,',
        '        Text(\n'
        '            text = stringResource(id = R.string.setup_permissions_title),\n'
        '            style = MaterialTheme.typography.headlineMedium,',
    ),
    # Notifications PermissionItem
    (
        '                PermissionItem(\n'
        '                    title = "Notifications",\n'
        '                    description = "Required for reminders and sync status.",',
        '                PermissionItem(\n'
        '                    title = stringResource(id = R.string.setup_perm_notifications_title),\n'
        '                    description = stringResource(id = R.string.setup_perm_notifications_desc),',
    ),
    # Exact Alarms PermissionItem
    (
        '            PermissionItem(\n'
        '                title = "Exact Alarms",\n'
        '                description = "Ensures reminders are triggered precisely.",',
        '            PermissionItem(\n'
        '                title = stringResource(id = R.string.setup_perm_exact_alarms_title),\n'
        '                description = stringResource(id = R.string.setup_perm_exact_alarms_desc),',
    ),
    # Progress text
    (
        '            Text(\n'
        '                text = "Progress: $grantedCount/$totalCount",\n'
        '                style = MaterialTheme.typography.labelLarge,',
        '            Text(\n'
        '                text = stringResource(id = R.string.setup_progress, grantedCount, totalCount),\n'
        '                style = MaterialTheme.typography.labelLarge,',
    ),
    # All ready
    (
        '                    Text("All ready to go!", fontWeight = FontWeight.Black, color = Color(0xFF2E7D32))',
        '                    Text(stringResource(id = R.string.setup_all_ready), fontWeight = FontWeight.Black, color = Color(0xFF2E7D32))',
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

with open(PATH, "w", encoding="utf-8", newline="") as f:
    f.write(content)

print(f"Applied: {applied}/{len(REPLACEMENTS)} edits")
if missing:
    print("MISSING:")
    for m in missing:
        print(f"  {m}")
