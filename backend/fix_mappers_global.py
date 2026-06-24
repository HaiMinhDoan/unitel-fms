import os
import re

mapper_dir = r'src\main\java\com\unitel\fms\backend\mappers'
mapping_pattern = re.compile(r'@Mapping\(\s*target\s*=\s*"([^"]+)\.id"\s*,\s*source\s*=\s*"([^"]+)"\s*\)')
mapper_pattern = re.compile(r'@Mapper\(([^)]*)\)')

for root, _, files in os.walk(mapper_dir):
    for file in files:
        if file.endswith("Mapper.java") and "Impl" not in file and file != "MapperUtils.java":
            filepath = os.path.join(root, file)
            with open(filepath, 'r', encoding='utf-8') as f:
                content = f.read()
            
            # Replace target="X.id" -> target="X"
            if not mapping_pattern.search(content):
                # Even if no X.id, maybe we want to add uses = MapperUtils.class? 
                # Let's add it anyway just in case it maps other UUIDs (like DTO IDs).
                pass
            
            new_content = mapping_pattern.sub(r'@Mapping(target = "\1", source = "\2")', content)
            
            # Add uses = MapperUtils.class to @Mapper
            def add_uses(match):
                params = match.group(1)
                if 'uses' not in params:
                    if params.strip():
                        return f'@Mapper({params}, uses = MapperUtils.class)'
                    else:
                        return '@Mapper(uses = MapperUtils.class)'
                else:
                    if 'MapperUtils.class' not in params:
                        # find 'uses = { ... }' or 'uses = X.class'
                        # It's a bit tricky with regex, let's do a simple string replace if 'uses = ' exists
                        if 'uses = {' in params:
                            return f'@Mapper({params.replace("uses = {", "uses = {MapperUtils.class, ")})'
                        else:
                            return f'@Mapper({params.replace("uses = ", "uses = {MapperUtils.class, ").replace(".class", ".class}")})'
                return match.group(0)

            new_content = mapper_pattern.sub(add_uses, new_content)
            
            with open(filepath, 'w', encoding='utf-8') as f:
                f.write(new_content)
            print(f"Processed {file}")
