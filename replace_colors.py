import re

file_path = "app/src/main/java/com/example/myapplication/MainActivity.kt"

with open(file_path, "r") as f:
    content = f.read()

replacements = {
    "MSurface": "MaterialTheme.colorScheme.surface",
    "MDarkGray": "MaterialTheme.colorScheme.surfaceVariant",
    "MBlueDark": "MaterialTheme.colorScheme.primary",
    "MTextWhite": "MaterialTheme.colorScheme.onSurface",
    "MBlueLight": "MaterialTheme.colorScheme.secondary",
    "MTextMuted": "MaterialTheme.colorScheme.onSurfaceVariant",
    "MRed": "MaterialTheme.colorScheme.error",
    "MBorderGray": "MaterialTheme.colorScheme.outline",
    "MBlack": "MaterialTheme.colorScheme.background"
}

for old, new in replacements.items():
    content = re.sub(r'\b' + old + r'\b', new, content)

with open(file_path, "w") as f:
    f.write(content)

print("Replaced colors successfully.")
