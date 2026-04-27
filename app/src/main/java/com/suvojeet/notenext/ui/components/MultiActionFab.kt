@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)
package com.suvojeet.notenext.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.ToggleFloatingActionButtonDefaults.animateIcon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import com.suvojeet.notenext.R
import com.suvojeet.notenext.ui.theme.ThemeMode

/**
 * Multi-action FAB built on M3 Expressive [FloatingActionButtonMenu].
 *
 * Public signature is unchanged so existing call sites in NotesScreen and
 * ProjectNotesScreen do not need to be touched. The legacy [themeMode] and
 * [isScrollExpanded] parameters are kept for source-compatibility but the
 * Expressive menu handles its own theming and sizing.
 */
@Composable
fun MultiActionFab(
    isExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onNoteClick: () -> Unit,
    onChecklistClick: () -> Unit,
    onProjectClick: () -> Unit,
    onDrawingClick: () -> Unit = {},
    onTodoClick: () -> Unit = {},
    showProjectButton: Boolean = true,
    @Suppress("UNUSED_PARAMETER") themeMode: ThemeMode,
    @Suppress("UNUSED_PARAMETER") isScrollExpanded: Boolean = true,
) {
    val items = buildList {
        add(FabAction(Icons.Default.Note, stringResource(R.string.note), onNoteClick))
        add(FabAction(Icons.Default.CheckBox, stringResource(R.string.checklist), onChecklistClick))
        add(FabAction(Icons.Default.Brush, stringResource(R.string.drawing), onDrawingClick))
        add(FabAction(Icons.Default.TaskAlt, stringResource(R.string.todo), onTodoClick))
        if (showProjectButton) {
            add(FabAction(Icons.Default.CreateNewFolder, stringResource(R.string.projects), onProjectClick))
        }
    }

    FloatingActionButtonMenu(
        expanded = isExpanded,
        button = {
            ToggleFloatingActionButton(
                checked = isExpanded,
                onCheckedChange = onExpandedChange,
            ) {
                val imageVector by animateIcon(
                    checkedPainter = rememberVectorPainter(Icons.Filled.Close),
                    uncheckedPainter = rememberVectorPainter(Icons.Filled.Add),
                )
                Icon(
                    painter = imageVector,
                    contentDescription = stringResource(id = R.string.add),
                )
            }
        },
    ) {
        items.forEach { action ->
            FloatingActionButtonMenuItem(
                onClick = {
                    action.onClick()
                    onExpandedChange(false)
                },
                icon = { Icon(action.icon, contentDescription = action.label) },
                text = { Text(action.label) },
            )
        }
    }
}

private data class FabAction(
    val icon: ImageVector,
    val label: String,
    val onClick: () -> Unit,
)
