@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)
package com.suvojeet.notenext.ui.add_edit_note.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.suvojeet.notenext.data.ai.ExtractedReminder
import com.suvojeet.notenext.ui.components.springPress
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Inline suggestion shown when the smart-reminder feature spots a date in
 * the user's note. Tapping "Set reminder" pipes the extracted timestamp +
 * label up to the editor, which opens the existing reminder dialog with
 * the values pre-filled.
 */
@Composable
fun AiSmartReminderChip(
    visible: Boolean,
    reminder: ExtractedReminder?,
    onSetReminder: (ExtractedReminder) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible && reminder != null,
        enter = expandVertically(),
        exit = shrinkVertically(),
        modifier = modifier
    ) {
        if (reminder == null) return@AnimatedVisibility
        val fmt = remember { SimpleDateFormat("EEE d MMM, h:mm a", Locale.getDefault()) }
        val whenLabel = remember(reminder.timestampMs) { fmt.format(Date(reminder.timestampMs)) }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
            shape = MaterialTheme.shapes.large
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.error.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Rounded.NotificationsActive,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Reminder detected",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Text(
                        "${reminder.text} · $whenLabel",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
                Spacer(Modifier.width(8.dp))
                FilledTonalButton(
                    onClick = { onSetReminder(reminder) },
                    modifier = Modifier.springPress(),
                    shape = MaterialTheme.shapes.large
                ) {
                    Text("Set", style = MaterialTheme.typography.labelMedium)
                }
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp).springPress()
                ) {
                    Icon(
                        Icons.Rounded.Close,
                        contentDescription = "Dismiss",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
