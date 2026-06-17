@file:OptIn(ExperimentalMaterial3Api::class)
package com.suvojeet.notenext.ui.add_edit_note.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.suvojeet.notenext.data.NoteSummaryWithAttachments
import com.suvojeet.notenext.ui.components.springPress
import kotlin.math.roundToInt

/**
 * A focused bottom sheet for manually ordering the pinned section.
 *
 * Each row carries a drag handle that uses the same battle-tested 1‑D swap
 * gesture as the checklist editor — long, single-column drags are predictable
 * in a way a 2‑D staggered-grid drag is not. The new order is committed once
 * the user taps Done, so a single DB write batch lands instead of one per swap.
 */
@Composable
fun PinnedReorderSheet(
    pinnedNotes: List<NoteSummaryWithAttachments>,
    onDismiss: () -> Unit,
    onConfirm: (orderedIds: List<Int>) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    // Local working copy; the grid keeps showing the old order until Done.
    val items = remember(pinnedNotes) {
        mutableStateListOf<NoteSummaryWithAttachments>().apply { addAll(pinnedNotes) }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.PushPin,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "Reorder pinned notes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = "Hold the handle and drag to rearrange.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp)
                    .padding(top = 8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                itemsIndexed(items, key = { _, it -> it.note.id }) { index, item ->
                    val dragOffset = remember { mutableStateOf(0f) }
                    val isDragging = dragOffset.value != 0f
                    val currentItems by rememberUpdatedState(items)
                    val currentIndex by rememberUpdatedState(index)

                    val density = LocalDensity.current
                    val rowHeightPx = remember(density) { with(density) { 60.dp.toPx() } }
                    val haptic = LocalHapticFeedback.current

                    val dragModifier = Modifier.pointerInput(Unit) {
                        detectDragGestures(
                            onDrag = { change, dragAmount ->
                                change.consume()
                                dragOffset.value += dragAmount.y
                                val i = currentIndex
                                if (dragOffset.value > rowHeightPx) {
                                    if (i < currentItems.lastIndex) {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        val tmp = currentItems[i]
                                        currentItems[i] = currentItems[i + 1]
                                        currentItems[i + 1] = tmp
                                        dragOffset.value -= rowHeightPx
                                    }
                                } else if (dragOffset.value < -rowHeightPx) {
                                    if (i > 0) {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        val tmp = currentItems[i]
                                        currentItems[i] = currentItems[i - 1]
                                        currentItems[i - 1] = tmp
                                        dragOffset.value += rowHeightPx
                                    }
                                }
                            },
                            onDragEnd = { dragOffset.value = 0f },
                            onDragCancel = { dragOffset.value = 0f }
                        )
                    }

                    PinnedReorderRow(
                        note = item,
                        dragModifier = dragModifier,
                        modifier = Modifier
                            .then(if (isDragging) Modifier else Modifier.animateItem())
                            .offset { IntOffset(0, dragOffset.value.roundToInt()) }
                            .zIndex(if (isDragging) 1f else 0f)
                            .graphicsLayer {
                                if (isDragging) {
                                    scaleX = 1.02f
                                    scaleY = 1.02f
                                    shadowElevation = 8f
                                }
                            }
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss, modifier = Modifier.springPress()) {
                    Text("Cancel")
                }
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = { onConfirm(items.map { it.note.id }) },
                    modifier = Modifier.springPress()
                ) {
                    Text("Done")
                }
            }
        }
    }
}

@Composable
private fun PinnedReorderRow(
    note: NoteSummaryWithAttachments,
    dragModifier: Modifier,
    modifier: Modifier = Modifier
) {
    val n = note.note
    val title = when {
        n.isLocked || n.isEncrypted -> n.title.ifBlank { "Protected note" }
        n.title.isNotBlank() -> n.title
        n.content.isNotBlank() -> n.content.lineSequence().firstOrNull()?.trim().orEmpty().ifBlank { "Untitled note" }
        else -> "Untitled note"
    }
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainerHighest,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Color dot — quick visual identity, matches the card's color.
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(
                        if (n.color != 0) Color(n.color) else MaterialTheme.colorScheme.outlineVariant
                    )
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(12.dp))
            Icon(
                imageVector = Icons.Filled.DragHandle,
                contentDescription = "Drag to reorder",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .size(24.dp)
                    .then(dragModifier)
            )
        }
    }
}
