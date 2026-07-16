import sys

file_path = "app/src/main/java/com/example/myapplication/MainActivity.kt"

with open(file_path, "r") as f:
    content = f.read()

# Add navigateReplace function
if "fun navigateReplace(screen: Screen)" not in content:
    content = content.replace(
        "fun navigateToRoot(screen: Screen) { backStack.clear(); backStack.add(screen) }",
        "fun navigateToRoot(screen: Screen) { backStack.clear(); backStack.add(screen) }\n    fun navigateReplace(screen: Screen) { if (backStack.size > 1) backStack.removeAt(backStack.size - 1); backStack.add(screen) }"
    )

# Fix Login and Register navigation
content = content.replace(
    "onNavigateToRegister = { navigateTo(Screen.Register) }",
    "onNavigateToRegister = { navigateReplace(Screen.Register) }"
)

content = content.replace(
    "onNavigateToLogin = { navigateBack() }",
    "onNavigateToLogin = { navigateReplace(Screen.Login) }"
)

with open(file_path, "w") as f:
    f.write(content)

print("Navigation fixed in MainActivity.kt")
