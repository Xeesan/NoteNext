@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)
package com.suvojeet.notenext.ui.add_edit_note.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.suvojeet.notenext.R
import com.suvojeet.notenext.ui.components.springPress

/**
 * Google Keep–style editor bottom bar: a flat row with three tonal action
 * buttons on the left — "+" (attachment, rounded square), color palette and
 * "A" formatting (both circular) — and a plain "⋮" more button on the far
 * right. Reminder lives on the top bar (Keep's bell); undo/redo live in the
 * more-options sheet. Insets are owned by the enclosing Surface, so this bar
 * consumes none.
 */
@Composable
fun AddEditNoteBottomAppBar(
    showColorPicker: (Boolean) -> Unit,
    showFormatBar: (Boolean) -> Unit,
    showMoreOptions: (Boolean) -> Unit,
    onAttachmentClick: () -> Unit,
    backgroundColor: Color = MaterialTheme.colorScheme.surface
) {
    val tonalColors = IconButtonDefaults.filledTonalIconButtonColors(
        containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        contentColor = MaterialTheme.colorScheme.onSurface
    )

    BottomAppBar(
        containerColor = backgroundColor,
        windowInsets = WindowInsets(0, 0, 0, 0),
        modifier = Modifier.height(64.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // "+" attachment — rounded square, like Keep
                FilledTonalIconButton(
                    onClick = onAttachmentClick,
                    modifier = Modifier.springPress(),
                    shape = MaterialTheme.shapes.medium,
                    colors = tonalColors
                ) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(id = R.string.add_attachment))
                }
                // Color palette — circular
                FilledTonalIconButton(
                    onClick = { showColorPicker(true) },
                    modifier = Modifier.springPress(),
                    colors = tonalColors
                ) {
                    Icon(Icons.Default.Palette, contentDescription = stringResource(id = R.string.toggle_color_picker))
                }
                // "A" formatting — circular
                FilledTonalIconButton(
                    onClick = { showFormatBar(true) },
                    modifier = Modifier.springPress(),
                    colors = tonalColors
                ) {
                    Icon(Icons.Default.TextFields, contentDescription = stringResource(id = R.string.toggle_format_bar))
                }
            }

            // "⋮" more options — plain, far right
            IconButton(
                onClick = { showMoreOptions(true) },
                modifier = Modifier.springPress()
            ) {
                Icon(Icons.Default.MoreVert, contentDescription = stringResource(id = R.string.more_options))
            }
        }
    }
}
