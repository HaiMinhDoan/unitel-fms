import os
import re

dtos_dir = 'src/main/java/com/unitel/fms/backend/dtos/request'

for root, _, files in os.walk(dtos_dir):
    for file in files:
        if file.endswith('Request.java'):
            filepath = os.path.join(root, file)
            with open(filepath, 'r', encoding='utf-8') as f:
                content = f.read()
            
            # Add import
            if '@NotNull' not in content:
                content = content.replace('import lombok.Data;', 'import lombok.Data;\nimport jakarta.validation.constraints.NotNull;')
            
            # Match private UUID somethingId; and add @NotNull
            content = re.sub(r'(?<!@NotNull\n)\s+private UUID ([a-zA-Z]+Id);', r'\n    @NotNull(message = "\1 is required")\n    private java.util.UUID \1;', content)
            
            # Also apply @NotNull to specific strings that shouldn't be null
            content = re.sub(r'(?<!@NotBlank\n)(?<!@NotNull\n)\s+private String (plateNumber|name|docType);', r'\n    @jakarta.validation.constraints.NotBlank(message = "\1 is required")\n    private String \1;', content)

            with open(filepath, 'w', encoding='utf-8') as f:
                f.write(content)
