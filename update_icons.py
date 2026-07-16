import os
from PIL import Image

# Path to the generated image
source_image_path = "/home/apknation/.gemini/antigravity/brain/728f496f-1dab-4a04-918a-0b9983d8a6b6/majisalama_app_icon_1784216619593.png"

# Base mipmap directory
res_dir = "app/src/main/res"

# Mipmap definitions: (folder_name, legacy_size, adaptive_size)
mipmaps = {
    "mipmap-mdpi": (48, 108),
    "mipmap-hdpi": (72, 162),
    "mipmap-xhdpi": (96, 216),
    "mipmap-xxhdpi": (144, 324),
    "mipmap-xxxhdpi": (192, 432),
}

# Open the original image
try:
    img = Image.open(source_image_path).convert("RGBA")
except Exception as e:
    print(f"Error opening image: {e}")
    exit(1)

for folder, (legacy_size, adaptive_size) in mipmaps.items():
    folder_path = os.path.join(res_dir, folder)
    os.makedirs(folder_path, exist_ok=True)
    
    # 1. Create legacy icon (ic_launcher.png and ic_launcher_round.png)
    legacy_img = img.resize((legacy_size, legacy_size), Image.Resampling.LANCZOS)
    legacy_img.save(os.path.join(folder_path, "ic_launcher.png"))
    legacy_img.save(os.path.join(folder_path, "ic_launcher_round.png"))
    
    # 2. Create foreground for adaptive icon (ic_launcher_foreground.png)
    # The generated icon already has a background, but we can just use it as the foreground.
    # Adaptive icons scale the foreground down, so the logo will be centered.
    adaptive_img = img.resize((adaptive_size, adaptive_size), Image.Resampling.LANCZOS)
    adaptive_img.save(os.path.join(folder_path, "ic_launcher_foreground.png"))

print("Icons resized and saved successfully.")

