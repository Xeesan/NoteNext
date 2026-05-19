@file:OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
package com.suvojeet.notenext.ui.add_edit_note.components

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.suvojeet.notenext.R
import com.suvojeet.notenext.ui.components.springPress
import com.suvojeet.notenext.ui.notes.NotesEvent
import com.suvojeet.notenext.ui.notes.NotesEditState
import com.suvojeet.notenext.core.model.NoteType
import com.suvojeet.notenext.data.ChecklistItem
import java.text.SimpleDateFormat
import java.util.*

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun MoreOptionsSheet(
    state: NotesEditState,
    onEvent: (NotesEvent) -> Unit,
    onDismiss: () -> Unit,
    showDeleteDialog: (Boolean) -> Unit,
    showSaveAsDialog: (Boolean) -> Unit,
    showHistoryDialog: (Boolean) -> Unit,
    showExpiryDialog: (Boolean) -> Unit,
    onPrint: () -> Unit,
    onToggleLock: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault()) }
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    val lockLabel = stringResource(id = if (state.editingIsLocked) R.string.unlock else R.string.lock)
    val lockIcon = if (state.editingIsLocked) Icons.Default.LockOpen else Icons.Default.Lock
    
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary
    val errorColor = MaterialTheme.colorScheme.error

    // Labels for the grid
    val options = remember(state.editingIsLocked, state.editingNoteType, state.editingIsNewNote, primaryColor, secondaryColor, tertiaryColor, errorColor) {
        mutableListOf<OptionItem>().apply {
            add(OptionItem(lockLabel, lockIcon, primaryColor) { onToggleLock() })
            add(OptionItem("Self-Destruct", Icons.Default.Timer, errorColor) { showExpiryDialog(true) })
            add(OptionItem(if (state.editingNoteType == NoteType.TEXT) "List View" else "Text View", Icons.Default.SwapHoriz, secondaryColor) { onEvent(NotesEvent.OnToggleNoteType) })
            add(OptionItem("Search", Icons.Default.Search, primaryColor) { onEvent(NotesEvent.ToggleNoteSearch) })
            add(OptionItem("Labels", Icons.AutoMirrored.Filled.Label, tertiaryColor) { onEvent(NotesEvent.OnAddLabelsToCurrentNoteClick) })
            add(OptionItem("Share", Icons.Default.Share, primaryColor) {
                val shareContent = if (state.editingNoteType == NoteType.CHECKLIST) {
                    val sb = StringBuilder()
                    state.editingChecklist.forEach { item ->
                        val status = if (item.isChecked) "[x]" else "[ ]"
                        sb.append("$status ${item.text}\n")
                    }
                    sb.toString()
                } else {
                    state.editingContent.text
                }

                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, state.editingTitle + "\n\n" + shareContent)
                    type = "text/plain"
                }
                context.startActivity(Intent.createChooser(sendIntent, null))
            })
            add(OptionItem("Copy", Icons.Default.ContentCopy, secondaryColor) { onEvent(NotesEvent.OnCopyCurrentNoteClick) })
            add(OptionItem("Print", Icons.Default.Print, secondaryColor) { onPrint() })
            add(OptionItem("Export", Icons.Default.FileDownload, tertiaryColor) { showSaveAsDialog(true) })
            if (!state.editingIsNewNote) {
                add(OptionItem("History", Icons.Default.History, secondaryColor) { showHistoryDialog(true) })
            }
            add(OptionItem("Extract Tasks", Icons.Default.AutoFixHigh, tertiaryColor) { onEvent(NotesEvent.ExtractActionItems) })
            add(OptionItem("To Todo", Icons.Default.PlaylistAddCheck, tertiaryColor) { onEvent(NotesEvent.ConvertToTodo) })
            add(OptionItem("Delete", Icons.Default.Delete, errorColor) { showDeleteDialog(true) })
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            if (!state.editingIsNewNote && state.editingLastEdited != null && state.editingLastEdited != 0L) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Last edited ${dateFormat.format(Date(state.editingLastEdited))}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(options) { option ->
                    GridOptionItem(option) {
                        onDismiss()
                        option.action()
                    }
                }
            }
        }
    }
}

private data class OptionItem(
    val label: String,
    val icon: ImageVector,
    val color: Color,
    val action: () -> Unit
)

@Composable
private fun GridOptionItem(
    option: OptionItem,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .springPress()
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .background(option.color.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = option.icon,
                contentDescription = option.label,
                tint = option.color,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = option.label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}
