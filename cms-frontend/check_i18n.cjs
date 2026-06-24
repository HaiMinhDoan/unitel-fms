const fs = require('fs');
const path = require('path');

const srcDir = path.join(__dirname, 'src');
const localesDir = path.join(srcDir, 'locales', 'lang');

// Helper to flatten JSON object to dot notation
function flattenObj(obj, parent = '', res = {}) {
  for (let key in obj) {
    if (obj.hasOwnProperty(key)) {
      let propName = parent ? parent + '.' + key : key;
      if (typeof obj[key] === 'object' && obj[key] !== null) {
        flattenObj(obj[key], propName, res);
      } else {
        res[propName] = obj[key];
      }
    }
  }
  return res;
}

// Find all used keys in src directory
function findUsedKeys(dir, keys = new Set()) {
  const files = fs.readdirSync(dir);
  for (const file of files) {
    const fullPath = path.join(dir, file);
    const stat = fs.statSync(fullPath);
    if (stat.isDirectory()) {
      findUsedKeys(fullPath, keys);
    } else if (fullPath.endsWith('.ts') || fullPath.endsWith('.vue') || fullPath.endsWith('.tsx')) {
      const content = fs.readFileSync(fullPath, 'utf8');
      // match t('fms.xxx.yyy') or t("fms.xxx.yyy")
      const regex = /t\(['"](fms\.[^'"]+)['"]\)/g;
      let match;
      while ((match = regex.exec(content)) !== null) {
        keys.add(match[1]);
      }
    }
  }
  return keys;
}

const usedKeys = findUsedKeys(srcDir);
const usedKeysArray = Array.from(usedKeys);

const langs = ['en', 'vi', 'lo'];
const missingKeys = {};

langs.forEach(lang => {
  const fmsPath = path.join(localesDir, lang, 'fms.json');
  if (fs.existsSync(fmsPath)) {
    const fmsJson = JSON.parse(fs.readFileSync(fmsPath, 'utf8'));
    // Since 'fms.json' represents the 'fms' namespace, the keys inside it are mapped to 'fms.*'
    const flattened = flattenObj(fmsJson);
    const availableKeys = new Set(Object.keys(flattened).map(k => 'fms.' + k));
    
    missingKeys[lang] = usedKeysArray.filter(k => !availableKeys.has(k));
  } else {
    missingKeys[lang] = usedKeysArray;
  }
});

console.log(JSON.stringify(missingKeys, null, 2));
