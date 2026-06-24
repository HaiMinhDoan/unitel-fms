import { useI18n } from '@/hooks/web/useI18n';
const { t } = useI18n();
import { BasicColumn, FormSchema } from '@/components/Table';

export const columns: BasicColumn[] = [
  { title: t('fms.common.id'), dataIndex: 'id', ifShow: false },
  { title: t('fms.common.code'), dataIndex: 'code', width: 120 },
  { title: t('fms.common.nameEn'), dataIndex: 'nameEn', width: 200 },
  { title: t('fms.common.nameVi'), dataIndex: 'nameVi', width: 200 },
];

export const searchFormSchema: FormSchema[] = [
  { field: 'code', label: t('fms.common.code'), component: 'Input', colProps: { span: 8 } },
  { field: 'nameEn', label: t('fms.common.nameEn'), component: 'Input', colProps: { span: 8 } },
];

export const formSchema: FormSchema[] = [
  { field: 'id', label: t('fms.common.id'), component: 'Input', show: false },
  { field: 'code', label: t('fms.common.code'), component: 'Input', required: true },
  { field: 'nameEn', label: t('fms.common.nameEn'), component: 'Input', required: true },
  { field: 'nameVi', label: t('fms.common.nameVi'), component: 'Input' },
];
