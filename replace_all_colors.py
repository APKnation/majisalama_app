import os
import re

directory = "app/src/main/java/com/example/myapplication"

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

for root, dirs, files in os.walk(directory):
    for file in files:
        if file.endswith(".kt"):
            file_path = os.path.join(root, file)
            with open(file_path, "r", encoding="utf-8") as f:
                content = f.read()
            
            new_content = content
            for old, new in replacements.items():
                new_content = re.sub(r'\b' + old + r'\b', new, new_content)
            
            if new_content != content:
                with open(file_path, "w", encoding="utf-8") as f:
                    f.write(new_content)
                print(f"Updated {file_path}")

print("Replacement complete.")
