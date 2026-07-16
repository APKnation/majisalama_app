import os

def remove_border_color(file_path):
    with open(file_path, 'r') as f:
        content = f.read()
    
    import re
    # Remove borderColor = ... from MCard calls
    # Usually looks like: MCard(..., borderColor = Color(...)) or MCard(borderColor = ...)
    # Regex to match borderColor = [anything up to comma or parenthesis]
    # We will just do a simple replacement for the known problematic lines.
    
    # DashboardScreen.kt:157: MCard(..., borderColor = ...)
    new_content = re.sub(r'borderColor\s*=\s*[^,)]*(,)?\s*', '', content)
    
    with open(file_path, 'w') as f:
        f.write(new_content)
        
remove_border_color('app/src/main/java/com/example/myapplication/ui/screens/DashboardScreen.kt')
remove_border_color('app/src/main/java/com/example/myapplication/ui/screens/WaterSourceDetailsScreen.kt')
print("Removed borderColor arguments")
