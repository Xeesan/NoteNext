@file:OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
package com.suvojeet.notenext.ui.add_edit_note.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoFixHigh
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suvojeet.notenext.data.ai.ToneOption
import com.suvojeet.notenext.ui.components.springPress

/**
 * Tone rewriter bottom sheet.
 *
 * UX flow:
 *   1. User opens sheet from the AI menu in AddEditNoteScreen.
 *   2. They pick a tone (chip row).
 *   3. The original text on top, the rewritten text below — user can compare.
 *   4. Accept replaces the source text. Try-again re-runs with same tone.
 *   5. Closing without accepting discards the rewrite.
 */
@Composable
fun AiToneRewriteSheet(
    sourceText: String,
    rewrittenText: String?,
    isLoading: Boolean,
    selectedTone: ToneOption?,
    errorMessage: String?,
    onPickTone: (ToneOption) -> Unit,
    onAccept: () -> Unit,
    onTryAgain: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp).padding(bottom = 24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Rounded.AutoFixHigh,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Rewrite tone", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(
                        text = if (selectedTone != null) "Rewriting in ${selectedTone.displayName.lowercase()} tone" else "Pick a tone below",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onDismiss, modifier = Modifier.springPress()) {
                    Icon(Icons.Rounded.Close, contentDescription = "Close")
                }
            }

            Spacer(Modifier.height(16.dp))

            // Tone chips
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ToneOption.values().forEach { tone ->
                    val isSelected = tone == selectedTone
                    FilterChip(
                        selected = isSelected,
                        onClick = { onPickTone(tone) },
                        label = {
                            Text("${tone.emoji} ${tone.displayName}")
                        },
                        modifier = Modifier.springPress()
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // Source text preview
            Text("Original", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    sourceText.take(800) + if (sourceText.length > 800) "…" else "",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(12.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            // Rewritten text / loading / error
            Text("Rewritten", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(4.dp))
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.padding(12.dp).fillMaxWidth()) {
                    when {
                        isLoading -> Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                            Spacer(Modifier.width(12.dp))
                            Text("Rewriting…", style = MaterialTheme.typography.bodyMedium)
                        }
                        errorMessage != null -> Text(
                            errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        rewrittenText != null -> Text(
                            rewrittenText,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        selectedTone == null -> Text(
                            "Pick a tone above to see the rewrite.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Actions
            AnimatedVisibility(
                visible = rewrittenText != null && !isLoading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onTryAgain,
                        modifier = Modifier.weight(1f).springPress(),
                        shape = MaterialTheme.shapes.large,
                        enabled = selectedTone != null
                    ) {
                        Icon(Icons.Rounded.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Try again")
                    }
                    Button(
                        onClick = onAccept,
                        modifier = Modifier.weight(1f).springPress(),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Icon(Icons.Rounded.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Apply")
                    }
                }
            }
        }
    }
}

