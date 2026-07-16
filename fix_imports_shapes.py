import os
import glob

screens_dir = "app/src/main/java/com/example/myapplication/ui/screens/**/*.kt"

files = glob.glob(screens_dir, recursive=True)

for file in files:
    with open(file, "r") as f:
        content = f.read()
    
    if "import androidx.compose.ui.graphics.RoundedCornerShape(16.dp)" in content:
        new_content = content.replace("import androidx.compose.ui.graphics.RoundedCornerShape(16.dp)", "import androidx.compose.foundation.shape.RoundedCornerShape")
        
        with open(file, "w") as f:
            f.write(new_content)
        print(f"Fixed {file}")

print("Done fixing broken imports.")
