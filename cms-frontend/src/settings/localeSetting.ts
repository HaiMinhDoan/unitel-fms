import type { DropMenu } from '../components/Dropdown';
import type { LocaleSetting, LocaleType } from '#/config';

export const LOCALE: { [key: string]: LocaleType } = {
  VI_VN: 'vi',
  EN_US: 'en',
  LO_LA: 'lo',
};

export const localeSetting: LocaleSetting = {
  showPicker: true,
  // Locale
  locale: LOCALE.VI_VN,
  // Default locale
  fallback: LOCALE.VI_VN,
  // available Locales
  availableLocales: [LOCALE.VI_VN, LOCALE.EN_US, LOCALE.LO_LA],
};

// locale list
export const localeList: DropMenu[] = [
  {
    text: 'Tiếng Việt',
    event: LOCALE.VI_VN,
  },
  {
    text: 'English',
    event: LOCALE.EN_US,
  },
  {
    text: 'ພາສາລາວ',
    event: LOCALE.LO_LA,
  },
];
