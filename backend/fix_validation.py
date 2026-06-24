import os
import re

controllers_dir = 'src/main/java/com/unitel/fms/backend/controllers'

for root, _, files in os.walk(controllers_dir):
    for file in files:
        if file.endswith('Controller.java'):
            filepath = os.path.join(root, file)
            with open(filepath, 'r', encoding='utf-8') as f:
                content = f.read()
            
            # Add import if needed
            if '@Valid' not in content and ('@RequestBody' in content or '@ModelAttribute' in content):
                import_stmt = 'import jakarta.validation.Valid;\n'
                if import_stmt not in content:
                    content = content.replace('import org.springframework.web.bind.annotation.*;', 'import org.springframework.web.bind.annotation.*;\nimport jakarta.validation.Valid;')
            
            # Add @Valid to @RequestBody and @ModelAttribute
            content = re.sub(r'(@RequestBody\s+)', r'@Valid \1', content)
            content = re.sub(r'(@ModelAttribute\s+)', r'@Valid \1', content)
            
            with open(filepath, 'w', encoding='utf-8') as f:
                f.write(content)
