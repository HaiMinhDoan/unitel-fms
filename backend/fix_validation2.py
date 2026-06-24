import os
import re

controllers_dir = 'src/main/java/com/unitel/fms/backend/controllers'

for root, _, files in os.walk(controllers_dir):
    for file in files:
        if file.endswith('Controller.java'):
            filepath = os.path.join(root, file)
            with open(filepath, 'r', encoding='utf-8') as f:
                content = f.read()
            
            # Remove repeated @Valid
            content = re.sub(r'@Valid\s+@Valid', '@Valid', content)
            content = re.sub(r'@Valid\s+@Valid', '@Valid', content) # in case of triple
            
            with open(filepath, 'w', encoding='utf-8') as f:
                f.write(content)
