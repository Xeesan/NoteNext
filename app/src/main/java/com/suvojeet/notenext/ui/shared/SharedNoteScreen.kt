package com.suvojeet.notenext.ui.shared

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SharedNoteScreen(
    shareId: String,
    onBack: () -> Unit,
    viewModel: SharedNoteViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(shareId) { viewModel.start(shareId) }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is SharedNoteEvent.Toast -> Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                is SharedNoteEvent.SavedCopy -> { /* handled by snackbar/toast */ }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Shared note", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    ConnectionChip(connected = state.connected)
                    Spacer(Modifier.width(8.dp))
                }
            )
        },
        floatingActionButton = {
            if (!state.loading && state.error == null) {
                ExtendedFloatingActionButton(
                    onClick = { viewModel.saveCopy() },
                    icon = {
                        Icon(
                            if (state.savedLocally) Icons.Default.CheckCircle else Icons.Default.BookmarkAdd,
                            contentDescription = null
                        )
                    },
                    text = { Text(if (state.savedLocally) "Saved" else "Save a copy") }
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                state.loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                state.error != null -> {
                    ErrorState(message = state.error!!, onRetry = { viewModel.retry() })
                }
                else -> {
                    SharedNoteContent(state = state, viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
private fun SharedNoteContent(state: SharedNoteUiState, viewModel: SharedNoteViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Attribution / metadata card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            "Shared via NoteNext",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "by ${state.sharedBy}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                if (state.updatedAt != null || state.createdAt != null) {
                    Spacer(Modifier.height(8.dp))
                    val created = state.createdAt?.let { prettyDate(it) }
                    val updated = state.updatedAt?.let { prettyDate(it) }
                    Text(
                        buildString {
                            if (created != null) append("Created $created")
                            if (updated != null) {
                                if (isNotEmpty()) append("  ·  ")
                                append("Updated $updated")
                            }
                        },
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    "Edits sync live with everyone who has this link.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = state.title,
            onValueChange = { viewModel.onTitleChange(it) },
            label = { Text("Title") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = state.content,
            onValueChange = { viewModel.onContentChange(it) },
            label = { Text("Note") },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 240.dp)
        )

        Spacer(Modifier.height(96.dp)) // breathing room above the FAB
    }
}

@Composable
private fun ConnectionChip(connected: Boolean) {
    AssistChip(
        onClick = {},
        enabled = false,
        label = { Text(if (connected) "Live" else "Offline", style = MaterialTheme.typography.labelSmall) },
        leadingIcon = {
            Icon(
                if (connected) Icons.Default.CloudDone else Icons.Default.CloudOff,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            disabledLabelColor = if (connected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            disabledLeadingIconContentColor = if (connected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}

@Composable
private fun BoxScope.ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .align(Alignment.Center)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.CloudOff,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(48.dp)
        )
        Spacer(Modifier.height(12.dp))
        SelectionContainer {
            Text(
                message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(Modifier.height(16.dp))
        Button(onClick = onRetry) { Text("Retry") }
    }
}

/**
 * Best-effort prettifier for an ISO-8601 timestamp (e.g. "2026-06-29T10:15:30.123Z").
 * Uses plain string ops to stay safe on minSdk 24 (no java.time / desugaring needed).
 */
private fun prettyDate(iso: String): String = try {
    if (iso.length >= 16 && iso[10] == 'T') {
        iso.substring(0, 10) + " " + iso.substring(11, 16)
    } else {
        iso
    }
} catch (e: Exception) {
    iso
}
