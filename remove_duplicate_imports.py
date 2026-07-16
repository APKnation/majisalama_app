import os
import glob

screens_dir = "app/src/main/java/com/example/myapplication/ui/screens/**/*.kt"

files = glob.glob(screens_dir, recursive=True)

for file in files:
    with open(file, "r") as f:
        lines = f.readlines()
    
    new_lines = []
    imports = set()
    
    for line in lines:
        if line.startswith("import "):
            if line not in imports:
                imports.add(line)
                new_lines.append(line)
        else:
            new_lines.append(line)
            
    with open(file, "w") as f:
        f.writelines(new_lines)
    print(f"Fixed duplicates in {file}")

print("Done removing duplicate imports.")
