const fs = require('fs');
const path = require('path');

function processDir(dir) {
  const files = fs.readdirSync(dir);
  for (const file of files) {
    const fullPath = path.join(dir, file);
    const stat = fs.statSync(fullPath);
    if (stat.isDirectory()) {
      processDir(fullPath);
    } else if (fullPath.endsWith('.data.ts')) {
      let content = fs.readFileSync(fullPath, 'utf8');
      
      // Match value: 'UPPER_CASE' and replace with value: 'upper_case'
      const regex = /value:\s*'([A-Z_]+)'/g;
      let changed = false;
      
      content = content.replace(regex, (match, p1) => {
        changed = true;
        return `value: '${p1.toLowerCase()}'`;
      });
      
      if (changed) {
        fs.writeFileSync(fullPath, content);
        console.log(`Updated ${fullPath}`);
      }
    }
  }
}

processDir(path.join(__dirname, 'src', 'views', 'fms'));
