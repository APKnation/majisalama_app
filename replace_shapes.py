import os
import glob

screens_dir = "app/src/main/java/com/example/myapplication/ui/screens/**/*.kt"

files = glob.glob(screens_dir, recursive=True)

import_statement = "import androidx.compose.foundation.shape.RoundedCornerShape\n"

for file in files:
    with open(file, "r") as f:
        content = f.read()
    
    if "RectangleShape" in content:
        # Replace RectangleShape with RoundedCornerShape(16.dp)
        new_content = content.replace("RectangleShape", "RoundedCornerShape(16.dp)")
        
        # Add import if missing
        if "androidx.compose.foundation.shape.RoundedCornerShape" not in new_content:
            # Find the last import statement
            lines = new_content.split("\n")
            last_import_idx = -1
            for i, line in enumerate(lines):
                if line.startswith("import "):
                    last_import_idx = i
            
            if last_import_idx != -1:
                lines.insert(last_import_idx + 1, "import androidx.compose.foundation.shape.RoundedCornerShape")
                new_content = "\n".join(lines)
            else:
                new_content = import_statement + new_content
                
        with open(file, "w") as f:
            f.write(new_content)
        print(f"Updated {file}")

print("Done replacing RectangleShape with RoundedCornerShape(16.dp).")
