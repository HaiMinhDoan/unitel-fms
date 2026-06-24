import os
import re

controllers_dir = 'src/main/java/com/unitel/fms/backend/controllers'

for root, _, files in os.walk(controllers_dir):
    for file in files:
        if file.endswith('Controller.java'):
            filepath = os.path.join(root, file)
            with open(filepath, 'r', encoding='utf-8') as f:
                content = f.read()
            
            # Remove all @Valid and @jakarta.validation.Valid from the file except imports
            content = re.sub(r'@Valid\s+', '', content)
            content = re.sub(r'@jakarta\.validation\.Valid\s+', '', content)
            
            # Add @Valid explicitly before @RequestBody and @ModelAttribute
            content = re.sub(r'(@RequestBody\s+)', r'@Valid \1', content)
            content = re.sub(r'(@ModelAttribute\s+)', r'@Valid \1', content)
            
            with open(filepath, 'w', encoding='utf-8') as f:
                f.write(content)
