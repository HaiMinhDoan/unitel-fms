const fs = require('fs');
const path = require('path');

const localesDir = path.join(__dirname, 'src', 'locales', 'lang');

const translations = {
  en: { vehicleTypes: "Vehicle Types" },
  vi: { vehicleTypes: "Loại phương tiện" },
  lo: { vehicleTypes: "ປະເພດພາຫະນະ" }
};

['en', 'vi', 'lo'].forEach(lang => {
  const file = path.join(localesDir, lang, 'routes', 'fms.json');
  if (fs.existsSync(file)) {
    const data = JSON.parse(fs.readFileSync(file, 'utf8'));
    data.vehicleTypes = translations[lang].vehicleTypes;
    fs.writeFileSync(file, JSON.stringify(data, null, 2));
    console.log(`Updated ${lang}/routes/fms.json`);
  }
});
