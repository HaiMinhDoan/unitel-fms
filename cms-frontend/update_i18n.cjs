const fs = require('fs');
const path = require('path');

const localesDir = path.join(__dirname, 'src', 'locales', 'lang');

const translations = {
  en: {
    drivers: {
      docType: "Document Type",
      docNumber: "Document Number",
      issueDate: "Issue Date",
      expiryDate: "Expiry Date",
      driverId: "Driver ID"
    },
    common: {
      publicUrl: "Public URL"
    },
    routes: {
      partnerDrivers: "Partner Drivers",
      partnerVehicles: "Partner Vehicles"
    },
    trips: {
      incidentType: "Incident Type",
      severity: "Severity",
      description: "Description",
      reportedAt: "Reported At",
      tripId: "Trip ID"
    },
    vehicles: {
      vehicleType: "Vehicle Type"
    }
  },
  vi: {
    drivers: {
      docType: "Loại tài liệu",
      docNumber: "Số tài liệu",
      issueDate: "Ngày cấp",
      expiryDate: "Ngày hết hạn",
      driverId: "Mã tài xế"
    },
    common: {
      publicUrl: "Đường dẫn công khai"
    },
    routes: {
      partnerDrivers: "Tài xế đối tác",
      partnerVehicles: "Phương tiện đối tác"
    },
    trips: {
      incidentType: "Loại sự cố",
      severity: "Mức độ nghiêm trọng",
      description: "Mô tả",
      reportedAt: "Thời gian báo cáo",
      tripId: "Mã chuyến đi"
    },
    vehicles: {
      vehicleType: "Loại phương tiện"
    }
  },
  lo: {
    drivers: {
      docType: "ປະເພດເອກະສານ",
      docNumber: "ໝາຍເລກເອກະສານ",
      issueDate: "ວັນທີອອກ",
      expiryDate: "ວັນໝົດອາຍຸ",
      driverId: "ລະຫັດຄົນຂັບ"
    },
    common: {
      publicUrl: "ລິ້ງສາທາລະນະ"
    },
    routes: {
      partnerDrivers: "ຄົນຂັບຄູ່ຮ່ວມງານ",
      partnerVehicles: "ຍານພາຫະນະຄູ່ຮ່ວມງານ"
    },
    trips: {
      incidentType: "ປະເພດເຫດການ",
      severity: "ຄວາມຮ້າຍແຮງ",
      description: "ລາຍລະອຽດ",
      reportedAt: "ລາຍງານເວລາ",
      tripId: "ລະຫັດການເດີນທາງ"
    },
    vehicles: {
      vehicleType: "ປະເພດພາຫະນະ"
    }
  }
};

function deepMerge(target, source) {
  for (const key in source) {
    if (source[key] instanceof Object && !Array.isArray(source[key])) {
      if (!target[key]) Object.assign(target, { [key]: {} });
      deepMerge(target[key], source[key]);
    } else {
      target[key] = source[key];
    }
  }
}

['en', 'vi', 'lo'].forEach(lang => {
  const file = path.join(localesDir, lang, 'fms.json');
  if (fs.existsSync(file)) {
    const data = JSON.parse(fs.readFileSync(file, 'utf8'));
    deepMerge(data, translations[lang]);
    fs.writeFileSync(file, JSON.stringify(data, null, 2));
    console.log(`Updated ${lang}/fms.json`);
  }
});
