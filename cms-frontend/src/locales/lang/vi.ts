import { genMessage } from '../helper';
import antdLocale from 'ant-design-vue/es/locale/vi_VN';

const modules = import.meta.glob('./vi/**/*.{json,ts,js}', { eager: true });
export default {
  message: {
    ...genMessage(modules as Recordable<Recordable>, 'vi'),
    antdLocale,
  },
  dateLocale: null,
  dateLocaleName: 'vi',
};
