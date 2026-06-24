import os
import re

mapper_dir = r'src\main\java\com\unitel\fms\backend\mappers'

mapping_pattern = re.compile(r'@Mapping\(\s*target\s*=\s*"([^"]+)\.id"\s*,\s*source\s*=\s*"([^"]+)"\s*\)')

entity_package = "com.unitel.fms.backend.entities"

def capitalize(s):
    return s[0].upper() + s[1:]

for root, _, files in os.walk(mapper_dir):
    for file in files:
        if file.endswith("Mapper.java") and "Impl" not in file:
            filepath = os.path.join(root, file)
            with open(filepath, 'r', encoding='utf-8') as f:
                content = f.read()
            
            matches = mapping_pattern.findall(content)
            if not matches:
                continue
            
            # Replace mappings
            new_content = mapping_pattern.sub(r'@Mapping(target = "\1", source = "\2")', content)
            
            # Add mapping methods
            methods_to_add = []
            added_types = set()
            
            for target_obj, source_id in matches:
                entity_type = capitalize(target_obj)
                if entity_type not in added_types:
                    added_types.add(entity_type)
                    method_name = f"map{entity_type}"
                    method_code = f'''
    default com.unitel.fms.backend.entities.{entity_type} {method_name}(java.util.UUID id) {{
        if (id == null) return null;
        return com.unitel.fms.backend.entities.{entity_type}.builder().id(id).build();
    }}'''
                    methods_to_add.append(method_code)
            
            if methods_to_add:
                # find the last closing brace of the class/interface
                last_brace_idx = new_content.rfind('}')
                if last_brace_idx != -1:
                    methods_str = "\n".join(methods_to_add) + "\n"
                    new_content = new_content[:last_brace_idx] + methods_str + new_content[last_brace_idx:]
            
            with open(filepath, 'w', encoding='utf-8') as f:
                f.write(new_content)
            print(f"Fixed {file}")
