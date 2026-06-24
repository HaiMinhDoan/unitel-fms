import os
import re

controllers_dir = 'src/main/java/com/unitel/fms/backend/controllers'

for root, _, files in os.walk(controllers_dir):
    for file in files:
        if file.endswith('Controller.java'):
            filepath = os.path.join(root, file)
            with open(filepath, 'r', encoding='utf-8') as f:
                content = f.read()
            
            # Remove repeated @Valid around @RequestBody or @ModelAttribute
            content = re.sub(r'@Valid\s+@RequestBody\s+@Valid', '@Valid @RequestBody', content)
            content = re.sub(r'@Valid\s+@ModelAttribute\s+@Valid', '@Valid @ModelAttribute', content)
            content = re.sub(r'@RequestBody\s+@Valid\s+@Valid', '@Valid @RequestBody', content)
            content = re.sub(r'@ModelAttribute\s+@Valid\s+@Valid', '@Valid @ModelAttribute', content)
            
            with open(filepath, 'w', encoding='utf-8') as f:
                f.write(content)
