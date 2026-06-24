import { genMessage } from '../helper';
import antdLocale from 'ant-design-vue/es/locale/en_US'; // Fallback to EN for AntD since LO is not officially supported by Antd Vue

const modules = import.meta.glob('./lo/**/*.{json,ts,js}', { eager: true });
export default {
  message: {
    ...genMessage(modules as Recordable<Recordable>, 'lo'),
    antdLocale,
  },
  dateLocale: null,
  dateLocaleName: 'lo',
};
