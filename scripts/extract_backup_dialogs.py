"""Extract hardcoded strings from BackupDialogs.kt -> stringResource."""

PATH = "app/src/main/java/com/suvojeet/notenext/ui/components/BackupDialogs.kt"

# We need a helper composable map for strength labels — use a context fetch in the dialog.
REPLACEMENTS = [
    # PasswordSetDialog default param values: change to defaults using stringResource at call-site
    # Approach: Convert default String to nullable String? = null and resolve inside @Composable
    (
        '@Composable\n'
        'fun PasswordSetDialog(\n'
        '    onDismiss: () -> Unit,\n'
        '    onConfirm: (String) -> Unit,\n'
        '    title: String = "Set Backup Password",\n'
        '    confirmText: String = "Encrypt & Save"\n'
        ') {',
        '@Composable\n'
        'fun PasswordSetDialog(\n'
        '    onDismiss: () -> Unit,\n'
        '    onConfirm: (String) -> Unit,\n'
        '    title: String = stringResource(id = R.string.backup_password_set_title),\n'
        '    confirmText: String = stringResource(id = R.string.backup_password_set_confirm)\n'
        ') {',
    ),
    # AES-256 badge in PasswordSetDialog
    (
        '                        Text(\n'
        '                            text = "Secured with AES-256 Encryption",\n'
        '                            style = MaterialTheme.typography.labelSmall,',
        '                        Text(\n'
        '                            text = stringResource(id = R.string.backup_password_aes_badge),\n'
        '                            style = MaterialTheme.typography.labelSmall,',
    ),
    # Message
    (
        '                    Text(\n'
        '                        text = "Enter a password to encrypt your backup. You will need this password to restore it.",\n'
        '                        style = MaterialTheme.typography.bodyMedium,',
        '                    Text(\n'
        '                        text = stringResource(id = R.string.backup_password_set_message),\n'
        '                        style = MaterialTheme.typography.bodyMedium,',
    ),
    # Password label (first occurrence)
    (
        '                        onValueChange = { password = it; error = null },\n'
        '                        label = { Text("Password") },',
        '                        onValueChange = { password = it; error = null },\n'
        '                        label = { Text(stringResource(id = R.string.backup_password_label)) },',
    ),
    # Strength row: replace with localized strength label
    (
        '                            Text(\n'
        '                                text = "Strength: ${strength.name}",\n'
        '                                style = MaterialTheme.typography.labelSmall,',
        '                            Text(\n'
        '                                text = stringResource(id = R.string.backup_password_strength_label, strengthLabel),\n'
        '                                style = MaterialTheme.typography.labelSmall,',
    ),
    # Confirm Password label
    (
        '                        label = { Text("Confirm Password") },',
        '                        label = { Text(stringResource(id = R.string.backup_password_confirm_label)) },',
    ),
    # Cancel + Encrypt&Save action buttons in PasswordSetDialog
    (
        '                        TextButton(\n'
        '                            onClick = onDismiss,\n'
        '                            modifier = Modifier.springPress()\n'
        '                        ) {\n'
        '                            Text("Cancel")\n'
        '                        }\n'
        '                        Spacer(modifier = Modifier.width(8.dp))\n'
        '                        Button(\n'
        '                            onClick = {\n'
        '                                when {\n'
        '                                    password.isBlank() ->\n'
        '                                        error = "Password cannot be empty"\n'
        '                                    password.length < MIN_BACKUP_PASSWORD_LENGTH ->\n'
        '                                        error = "Password must be at least $MIN_BACKUP_PASSWORD_LENGTH characters"\n'
        '                                    strength == PasswordStrength.WEAK ->\n'
        '                                        error = "Password is too weak. Add digits, uppercase, or symbols."\n'
        '                                    password != confirmPassword ->\n'
        '                                        error = "Passwords do not match"\n'
        '                                    else ->\n'
        '                                        onConfirm(password)\n'
        '                                }\n'
        '                            },',
        '                        TextButton(\n'
        '                            onClick = onDismiss,\n'
        '                            modifier = Modifier.springPress()\n'
        '                        ) {\n'
        '                            Text(stringResource(id = R.string.cancel))\n'
        '                        }\n'
        '                        Spacer(modifier = Modifier.width(8.dp))\n'
        '                        Button(\n'
        '                            onClick = {\n'
        '                                when {\n'
        '                                    password.isBlank() ->\n'
        '                                        error = errEmpty\n'
        '                                    password.length < MIN_BACKUP_PASSWORD_LENGTH ->\n'
        '                                        error = errShortFn(MIN_BACKUP_PASSWORD_LENGTH)\n'
        '                                    strength == PasswordStrength.WEAK ->\n'
        '                                        error = errWeak\n'
        '                                    password != confirmPassword ->\n'
        '                                        error = errMismatch\n'
        '                                    else ->\n'
        '                                        onConfirm(password)\n'
        '                                }\n'
        '                            },',
    ),
    # PasswordSetDialog - confirm button text
    (
        '                            shape = RoundedCornerShape(12.dp)\n'
        '                        ) {\n'
        '                            Text(confirmText)\n'
        '                        }\n'
        '                    }\n'
        '                }\n'
        '            }\n'
        '        }\n'
        '    }\n'
        '}\n'
        '\n'
        '@Composable\n'
        'fun PasswordInputDialog(',
        '                            shape = RoundedCornerShape(12.dp)\n'
        '                        ) {\n'
        '                            Text(confirmText)\n'
        '                        }\n'
        '                    }\n'
        '                }\n'
        '            }\n'
        '        }\n'
        '    }\n'
        '}\n'
        '\n'
        '@Composable\n'
        'fun PasswordInputDialog(',  # No-op placeholder so ordering counts; we do it via separate replacement
    ),
    # PasswordInputDialog title
    (
        '                    Text(\n'
        '                        text = "Enter Password",\n'
        '                        style = MaterialTheme.typography.headlineSmall,',
        '                    Text(\n'
        '                        text = stringResource(id = R.string.backup_password_input_title),\n'
        '                        style = MaterialTheme.typography.headlineSmall,',
    ),
    # Backup encrypted with AES-256
    (
        '                        Text(\n'
        '                            text = "Backup encrypted with AES-256",\n'
        '                            style = MaterialTheme.typography.labelSmall,',
        '                        Text(\n'
        '                            text = stringResource(id = R.string.backup_password_input_aes_badge),\n'
        '                            style = MaterialTheme.typography.labelSmall,',
    ),
    # PasswordInputDialog message
    (
        '                    Text(\n'
        '                        text = "Please enter the password to unlock and restore your backup data.",\n'
        '                        style = MaterialTheme.typography.bodyMedium,',
        '                    Text(\n'
        '                        text = stringResource(id = R.string.backup_password_input_message),\n'
        '                        style = MaterialTheme.typography.bodyMedium,',
    ),
    # PasswordInputDialog Password label
    (
        '                        onValueChange = { password = it },\n'
        '                        label = { Text("Password") },',
        '                        onValueChange = { password = it },\n'
        '                        label = { Text(stringResource(id = R.string.backup_password_label)) },',
    ),
    # PasswordInputDialog Cancel + Unlock buttons
    (
        '                        TextButton(\n'
        '                            onClick = onDismiss,\n'
        '                            modifier = Modifier.springPress()\n'
        '                        ) {\n'
        '                            Text("Cancel")\n'
        '                        }\n'
        '                        Spacer(modifier = Modifier.width(8.dp))\n'
        '                        Button(\n'
        '                            onClick = { onConfirm(password) },\n'
        '                            modifier = Modifier.springPress(),\n'
        '                            shape = RoundedCornerShape(12.dp),\n'
        '                            enabled = password.isNotEmpty()\n'
        '                        ) {\n'
        '                            Text("Unlock & Restore")\n'
        '                        }',
        '                        TextButton(\n'
        '                            onClick = onDismiss,\n'
        '                            modifier = Modifier.springPress()\n'
        '                        ) {\n'
        '                            Text(stringResource(id = R.string.cancel))\n'
        '                        }\n'
        '                        Spacer(modifier = Modifier.width(8.dp))\n'
        '                        Button(\n'
        '                            onClick = { onConfirm(password) },\n'
        '                            modifier = Modifier.springPress(),\n'
        '                            shape = RoundedCornerShape(12.dp),\n'
        '                            enabled = password.isNotEmpty()\n'
        '                        ) {\n'
        '                            Text(stringResource(id = R.string.backup_password_unlock))\n'
        '                        }',
    ),
    # EncryptionInfoDialog title
    (
        '                Text("About Encrypted Backups")',
        '                Text(stringResource(id = R.string.backup_encryption_info_title))',
    ),
    # Intro text
    (
        '                Text(\n'
        '                    "Encrypted backups provide an extra layer of security for your data.",\n'
        '                    style = MaterialTheme.typography.bodyMedium,',
        '                Text(\n'
        '                    stringResource(id = R.string.backup_encryption_info_intro),\n'
        '                    style = MaterialTheme.typography.bodyMedium,',
    ),
    # 3 InfoBulletPoints
    (
        '                InfoBulletPoint(\n'
        '                    title = "How it works",\n'
        '                    description = "Your data is encrypted using the AES-256 standard before it leaves your device. This means your notes and attachments are scrambled into an unreadable format."\n'
        '                )',
        '                InfoBulletPoint(\n'
        '                    title = stringResource(id = R.string.backup_encryption_info_how_title),\n'
        '                    description = stringResource(id = R.string.backup_encryption_info_how_desc)\n'
        '                )',
    ),
    (
        '                InfoBulletPoint(\n'
        '                    title = "Security",\n'
        '                    description = "Even if someone gains access to your backup file (on Google Drive or your SD card), they cannot read your data without the correct password. Only you hold the key."\n'
        '                )',
        '                InfoBulletPoint(\n'
        '                    title = stringResource(id = R.string.backup_encryption_info_security_title),\n'
        '                    description = stringResource(id = R.string.backup_encryption_info_security_desc)\n'
        '                )',
    ),
    (
        '                InfoBulletPoint(\n'
        '                    title = "Important",\n'
        '                    description = "We do not store your backup password. If you lose it, you will not be able to restore your data from that backup. Please keep your password safe."\n'
        '                )',
        '                InfoBulletPoint(\n'
        '                    title = stringResource(id = R.string.backup_encryption_info_important_title),\n'
        '                    description = stringResource(id = R.string.backup_encryption_info_important_desc)\n'
        '                )',
    ),
    # Got it button
    (
        '                Text("Got it")',
        '                Text(stringResource(id = R.string.backup_encryption_info_got_it))',
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

# Inject the strength label and error helpers inside PasswordSetDialog after `var isPasswordVisible`
content = content.replace(
    '    var isPasswordVisible by remember { mutableStateOf(false) }\n'
    '\n'
    '    val strength = calculatePasswordStrength(password)',
    '    var isPasswordVisible by remember { mutableStateOf(false) }\n'
    '\n'
    '    val strengthWeak = stringResource(id = R.string.backup_password_strength_weak)\n'
    '    val strengthMedium = stringResource(id = R.string.backup_password_strength_medium)\n'
    '    val strengthStrong = stringResource(id = R.string.backup_password_strength_strong)\n'
    '    val errEmpty = stringResource(id = R.string.backup_password_error_empty)\n'
    '    val errWeak = stringResource(id = R.string.backup_password_error_weak)\n'
    '    val errMismatch = stringResource(id = R.string.backup_password_error_mismatch)\n'
    '    val errShortTemplate = stringResource(id = R.string.backup_password_error_short, MIN_BACKUP_PASSWORD_LENGTH)\n'
    '    val errShortFn: (Int) -> String = { _ -> errShortTemplate }\n'
    '\n'
    '    val strength = calculatePasswordStrength(password)\n'
    '    val strengthLabel = when (strength) {\n'
    '        PasswordStrength.WEAK -> strengthWeak\n'
    '        PasswordStrength.MEDIUM -> strengthMedium\n'
    '        PasswordStrength.STRONG -> strengthStrong\n'
    '    }',
)

if "import androidx.compose.ui.res.stringResource" not in content:
    content = content.replace(
        "import androidx.compose.ui.unit.sp",
        "import androidx.compose.ui.res.stringResource\nimport androidx.compose.ui.unit.sp",
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
