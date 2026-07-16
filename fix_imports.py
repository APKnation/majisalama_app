import os

directory = "app/src/main/java/com/example/myapplication"
import_statement = "import androidx.compose.material3.MaterialTheme\n"

for root, dirs, files in os.walk(directory):
    for file in files:
        if file.endswith(".kt"):
            file_path = os.path.join(root, file)
            with open(file_path, "r", encoding="utf-8") as f:
                content = f.read()
            
            if "MaterialTheme" in content and "import androidx.compose.material3.MaterialTheme" not in content:
                # find the last import and insert after it
                lines = content.split('\n')
                last_import_index = -1
                for i, line in enumerate(lines):
                    if line.startswith("import "):
                        last_import_index = i
                
                if last_import_index != -1:
                    lines.insert(last_import_index + 1, "import androidx.compose.material3.MaterialTheme")
                else:
                    # just insert after package
                    for i, line in enumerate(lines):
                        if line.startswith("package "):
                            lines.insert(i + 1, "\nimport androidx.compose.material3.MaterialTheme")
                            break
                            
                new_content = "\n".join(lines)
                with open(file_path, "w", encoding="utf-8") as f:
                    f.write(new_content)
                print(f"Added MaterialTheme import to {file_path}")

print("Import fix complete.")
