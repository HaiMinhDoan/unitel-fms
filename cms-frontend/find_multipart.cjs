const fs = require('fs');
const data = JSON.parse(fs.readFileSync('api_docs.json', 'utf8'));
const paths = data.paths;
const result = [];
for (const path in paths) {
  for (const method in paths[path]) {
    const details = paths[path][method];
    if (details.requestBody && details.requestBody.content && details.requestBody.content['multipart/form-data']) {
      result.push(`${method.toUpperCase()} ${path}`);
    }
  }
}
console.log(result.join('\n'));
