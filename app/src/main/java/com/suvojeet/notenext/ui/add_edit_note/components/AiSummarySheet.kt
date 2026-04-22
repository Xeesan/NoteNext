@file:OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
package com.suvojeet.notenext.ui.add_edit_note.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.suvojeet.notenext.ui.components.springPress
import kotlinx.coroutines.delay

@Composable
fun AiSummarySheet(
    summary: String?,
    isSummarizing: Boolean,
    onDismiss: () -> Unit,
    onClearSummary: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    // Set skipPartiallyExpanded to false to allow the two-stage swipe behavior
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    
    // Determine if we are in the expanded state to adjust UI elements
    val isExpanded = sheetState.targetValue == SheetValue.Expanded || sheetState.currentValue == SheetValue.Expanded
    
    val cornerRadius by animateDpAsState(
        targetValue = if (isExpanded) 0.dp else 28.dp,
        label = "CornerRadius"
    )
    
    val horizontalPadding by animateDpAsState(
        targetValue = if (isExpanded) 0.dp else 24.dp,
        label = "HorizontalPadding"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        shape = RoundedCornerShape(topStart = cornerRadius, topEnd = cornerRadius),
        tonalElevation = 6.dp,
        windowInsets = if (isExpanded) WindowInsets.statusBars else BottomSheetDefaults.windowInsets
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .then(if (isExpanded) Modifier.fillMaxHeight() else Modifier)
                .padding(horizontal = horizontalPadding)
                .padding(bottom = 24.dp)
        ) {
            // Header with Branding and Compact Actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = if (isExpanded) 24.dp else 0.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "NoteNext AI",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                if (!isSummarizing && summary != null) {
                    IconButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("AI Summary", summary)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.springPress()
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy Summary",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    IconButton(
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "Note Summary")
                                putExtra(Intent.EXTRA_TEXT, summary)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Summary"))
                        },
                        modifier = Modifier.springPress()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share Summary",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                IconButton(onClick = onDismiss, modifier = Modifier.springPress()) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            // Content Area
            Surface(
                shape = if (isExpanded) RoundedCornerShape(0.dp) else MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(if (isExpanded) 1f else 0f, fill = false)
                    .heightIn(min = 200.dp, max = if (isExpanded) 2000.dp else 450.dp)
            ) {
                Box(
                    modifier = Modifier.padding(8.dp),
                    contentAlignment = if (isSummarizing) Alignment.Center else Alignment.TopStart
                ) {
                    if (isSummarizing) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            LoadingIndicator(
                                modifier = Modifier.size(40.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Summarizing...", 
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        summary?.let { text ->
                            Column(
                                modifier = Modifier.verticalScroll(scrollState)
                            ) {
                                var visibleText by remember { mutableStateOf("") }
                                LaunchedEffect(text) {
                                    visibleText = "" 
                                    val chunkSize = 12
                                    for (i in text.indices step chunkSize) {
                                        val end = (i + chunkSize).coerceAtMost(text.length)
                                        visibleText += text.substring(i, end)
                                        delay(5) 
                                    }
                                    visibleText = text 
                                }
                                
                                MarkdownPreview(content = visibleText)
                            }
                        } ?: Text(
                            "No summary available.", 
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}
