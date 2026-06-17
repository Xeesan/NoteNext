@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)
package com.suvojeet.notenext.ui.add_edit_note.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.filled.AddLink
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.automirrored.filled.FormatIndentDecrease
import androidx.compose.material.icons.automirrored.filled.FormatIndentIncrease
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suvojeet.notenext.core.model.NoteType
import com.suvojeet.notenext.ui.notes.NotesEvent
import com.suvojeet.notenext.ui.notes.NotesEditState
import com.suvojeet.notenext.ui.theme.ThemeMode
import androidx.compose.ui.res.stringResource
import com.suvojeet.notenext.R
import com.suvojeet.notenext.ui.components.springPress
import androidx.compose.ui.text.SpanStyle

/**
 * Google Keep–style formatting panel.
 *
 * Two compact rows that sit above the keyboard:
 *  1. A segmented capsule of typographic glyph toggles — **B** / *I* / U̲ / S̶ —
 *     (each rendered in the style it applies, like Keep) plus quick tools on the
 *     right (bullet/indent, link, AI grammar fix).
 *  2. A scrollable strip of heading chips (Normal · H1 … H6) with the active
 *     level highlighted as a filled chip.
 *
 * Active styles animate their background so toggling feels tactile.
 */
@Composable
fun FormatToolbar(
    state: NotesEditState,
    onEvent: (NotesEvent) -> Unit,
    onInsertLinkClick: () -> Unit,
    onGrammarFixClick: () -> Unit,
    isFixingGrammar: Boolean,
    @Suppress("UNUSED_PARAMETER") themeMode: ThemeMode,
    modifier: Modifier = Modifier
) {
    val isChecklist = state.editingNoteType == NoteType.CHECKLIST

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // ── Row 1: character styles (segmented) + quick tools ──────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Segmented capsule — B / I / U / S as styled letters.
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceContainer
            ) {
                Row(
                    modifier = Modifier.padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    GlyphToggle(
                        glyph = "B",
                        fontWeight = FontWeight.Bold,
                        isActive = state.isBoldActive,
                        description = stringResource(id = R.string.bold_description),
                        onClick = { onEvent(NotesEvent.ApplyStyleToContent(SpanStyle(fontWeight = FontWeight.Bold))) }
                    )
                    GlyphToggle(
                        glyph = "I",
                        fontStyle = FontStyle.Italic,
                        isActive = state.isItalicActive,
                        description = stringResource(id = R.string.italic_description),
                        onClick = { onEvent(NotesEvent.ApplyStyleToContent(SpanStyle(fontStyle = FontStyle.Italic))) }
                    )
                    GlyphToggle(
                        glyph = "U",
                        textDecoration = TextDecoration.Underline,
                        isActive = state.isUnderlineActive,
                        description = stringResource(id = R.string.underline_description),
                        onClick = { onEvent(NotesEvent.ApplyStyleToContent(SpanStyle(textDecoration = TextDecoration.Underline))) }
                    )
                    GlyphToggle(
                        glyph = "S",
                        textDecoration = TextDecoration.LineThrough,
                        isActive = false,
                        description = "Strikethrough",
                        onClick = { onEvent(NotesEvent.ApplyStyleToContent(SpanStyle(textDecoration = TextDecoration.LineThrough))) }
                    )
                }
            }

            // Quick tools on the right.
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isChecklist) {
                    ToolButton(
                        icon = Icons.AutoMirrored.Filled.FormatIndentDecrease,
                        description = "Outdent",
                        onClick = { state.focusedChecklistItemId?.let { onEvent(NotesEvent.OutdentChecklistItem(it)) } }
                    )
                    ToolButton(
                        icon = Icons.AutoMirrored.Filled.FormatIndentIncrease,
                        description = "Indent",
                        onClick = { state.focusedChecklistItemId?.let { onEvent(NotesEvent.IndentChecklistItem(it)) } }
                    )
                } else {
                    ToolButton(
                        icon = Icons.Default.FormatListBulleted,
                        description = "Bulleted List",
                        onClick = { onEvent(NotesEvent.ApplyBulletedList) }
                    )
                    ToolButton(
                        icon = Icons.Default.AddLink,
                        description = stringResource(id = R.string.insert_link_description),
                        onClick = onInsertLinkClick
                    )
                }

                VerticalDivider(modifier = Modifier.height(20.dp).padding(horizontal = 4.dp))

                ToolButton(
                    icon = Icons.Outlined.AutoAwesome,
                    description = "Fix Grammar",
                    isActive = isFixingGrammar,
                    onClick = onGrammarFixClick
                )
            }
        }

        // ── Row 2: heading chips ───────────────────────────────────────────
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(HEADING_LEVELS) { level ->
                val label = when (level) {
                    0 -> stringResource(id = R.string.normal_text)
                    else -> "H$level"
                }
                FilterChip(
                    selected = state.activeHeadingStyle == level,
                    onClick = { onEvent(NotesEvent.ApplyHeadingStyle(level)) },
                    label = {
                        Text(
                            text = label,
                            fontSize = 13.sp,
                            fontWeight = if (level == 0) FontWeight.Normal else FontWeight.SemiBold
                        )
                    },
                    shape = CircleShape,
                    modifier = Modifier.springPress()
                )
            }
        }
    }
}

private val HEADING_LEVELS = listOf(0, 1, 2, 3, 4, 5, 6)

/**
 * A single character-style toggle that renders its own letter in the style it
 * applies, with an animated tonal pill behind it when active.
 */
@Composable
private fun GlyphToggle(
    glyph: String,
    isActive: Boolean,
    description: String,
    onClick: () -> Unit,
    fontWeight: FontWeight = FontWeight.Medium,
    fontStyle: FontStyle = FontStyle.Normal,
    textDecoration: TextDecoration = TextDecoration.None
) {
    val bg by animateColorAsState(
        targetValue = if (isActive) MaterialTheme.colorScheme.primary else androidx.compose.ui.graphics.Color.Transparent,
        animationSpec = spring(),
        label = "glyph_bg"
    )
    val fg by animateColorAsState(
        targetValue = if (isActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
        animationSpec = spring(),
        label = "glyph_fg"
    )
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(bg)
            .clickable(onClick = onClick)
            .springPress(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = glyph,
            color = fg,
            fontSize = 18.sp,
            fontWeight = fontWeight,
            fontStyle = fontStyle,
            textDecoration = textDecoration
        )
    }
}

/** A plain icon tool button used for bullet/indent/link/grammar actions. */
@Composable
private fun ToolButton(
    icon: ImageVector,
    description: String,
    onClick: () -> Unit,
    isActive: Boolean = false
) {
    val tint = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .springPress(),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = description, tint = tint, modifier = Modifier.size(22.dp))
    }
}
