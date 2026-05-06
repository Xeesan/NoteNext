"""Extract hardcoded strings from DrawingScreen.kt -> stringResource."""

PATH = "app/src/main/java/com/suvojeet/notenext/ui/drawing/DrawingScreen.kt"

REPLACEMENTS = [
    (
        'Icon(Icons.Default.Close, contentDescription = "Close")',
        'Icon(Icons.Default.Close, contentDescription = stringResource(id = R.string.drawing_close_cd))',
    ),
    (
        'Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = "Undo")',
        'Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = stringResource(id = R.string.drawing_undo_cd))',
    ),
    (
        'Icon(Icons.AutoMirrored.Filled.Redo, contentDescription = "Redo")',
        'Icon(Icons.AutoMirrored.Filled.Redo, contentDescription = stringResource(id = R.string.drawing_redo_cd))',
    ),
    (
        'Icon(Icons.Default.DeleteSweep, contentDescription = "Clear All")',
        'Icon(Icons.Default.DeleteSweep, contentDescription = stringResource(id = R.string.drawing_clear_all_cd))',
    ),
    (
        '                            Text("Save")',
        '                            Text(stringResource(id = R.string.save))',
    ),
    (
        '                            Text(\n'
        '                                "Start drawing here...",\n'
        '                                style = MaterialTheme.typography.bodyLarge.copy(',
        '                            Text(\n'
        '                                stringResource(id = R.string.drawing_placeholder),\n'
        '                                style = MaterialTheme.typography.bodyLarge.copy(',
    ),
    # Stroke size label (bottom bar)
    (
        '                        Text(\n'
        '                            "${state.currentStrokeWidth.toInt()}px",\n'
        '                            style = MaterialTheme.typography.labelLarge,',
        '                        Text(\n'
        '                            stringResource(id = R.string.drawing_stroke_size_label, state.currentStrokeWidth.toInt()),\n'
        '                            style = MaterialTheme.typography.labelLarge,',
    ),
    # Brush segmented button
    (
        '                        Text("Brush", style = MaterialTheme.typography.labelSmall)\n'
        '                    }\n'
        '                    SegmentedButton(\n'
        '                        selected = state.isEraserMode,',
        '                        Text(stringResource(id = R.string.drawing_tool_brush), style = MaterialTheme.typography.labelSmall)\n'
        '                    }\n'
        '                    SegmentedButton(\n'
        '                        selected = state.isEraserMode,',
    ),
    # Eraser segmented button
    (
        '                        Text("Eraser", style = MaterialTheme.typography.labelSmall)\n'
        '                    }\n'
        '                }\n'
        '\n'
        '                // Color Selection',
        '                        Text(stringResource(id = R.string.drawing_tool_eraser), style = MaterialTheme.typography.labelSmall)\n'
        '                    }\n'
        '                }\n'
        '\n'
        '                // Color Selection',
    ),
    # Settings toggle bottom bar
    (
        '                        contentDescription = "Settings"',
        '                        contentDescription = stringResource(id = R.string.drawing_settings_cd)',
    ),
    # SideBar Brush icon contentDescription
    (
        '                    Icon(Icons.Default.Brush, contentDescription = "Brush")\n'
        '                }\n'
        '                Text("Brush", style = MaterialTheme.typography.labelSmall)',
        '                    Icon(Icons.Default.Brush, contentDescription = stringResource(id = R.string.drawing_tool_brush))\n'
        '                }\n'
        '                Text(stringResource(id = R.string.drawing_tool_brush), style = MaterialTheme.typography.labelSmall)',
    ),
    # SideBar Eraser icon contentDescription
    (
        '                    Icon(Icons.Default.AutoFixHigh, contentDescription = "Eraser")\n'
        '                }\n'
        '                Text("Eraser", style = MaterialTheme.typography.labelSmall)',
        '                    Icon(Icons.Default.AutoFixHigh, contentDescription = stringResource(id = R.string.drawing_tool_eraser))\n'
        '                }\n'
        '                Text(stringResource(id = R.string.drawing_tool_eraser), style = MaterialTheme.typography.labelSmall)',
    ),
    # Stroke Width contentDescription
    (
        'Icon(Icons.Default.LineWeight, contentDescription = "Stroke Width")',
        'Icon(Icons.Default.LineWeight, contentDescription = stringResource(id = R.string.drawing_stroke_width_cd))',
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

with open(PATH, "w", encoding="utf-8", newline="") as f:
    f.write(content)

print(f"Applied: {applied}/{len(REPLACEMENTS)} edits")
if missing:
    print("MISSING:")
    for m in missing:
        print(f"  {m}")
