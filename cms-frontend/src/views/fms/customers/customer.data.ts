import { useI18n } from '@/hooks/web/useI18n';
const { t } = useI18n();
import { BasicColumn, FormSchema } from '@/components/Table';

export const columns: BasicColumn[] = [
  { title: t('fms.common.id'), dataIndex: 'id', ifShow: false },
  { title: t('fms.customers.code'), dataIndex: 'code', width: 120 },
  { title: t('fms.customers.name'), dataIndex: 'name', width: 200 },
  { title: t('fms.customers.code'), dataIndex: 'taxCode', width: 150 },
  { title: t('fms.common.address'), dataIndex: 'address', width: 250 },
  { title: t('fms.common.status'), dataIndex: 'status', width: 120 },
];

export const searchFormSchema: FormSchema[] = [
  { field: 'name', label: t('fms.customers.name'), component: 'Input', colProps: { span: 6 } },
  { field: 'code', label: t('fms.customers.code'), component: 'Input', colProps: { span: 6 } },
];

export const formSchema: FormSchema[] = [
  { field: 'id', label: t('fms.common.id'), component: 'Input', show: false },
  { field: 'code', label: t('fms.customers.code'), component: 'Input', required: true },
  { field: 'name', label: t('fms.customers.name'), component: 'Input', required: true },
  { field: 'taxCode', label: t('fms.customers.code'), component: 'Input' },
  { field: 'address', label: t('fms.common.address'), component: 'Input' },
  { field: 'contactPerson', label: t('fms.customers.contactPerson'), component: 'Input' },
  { field: 'contactPhone', label: t('fms.customers.contactPhone'), component: 'Input' },
  { field: 'status', label: t('fms.common.status'), component: 'Select', componentProps: { options: [{ label: t('fms.common.active'), value: 'active' }, { label: t('fms.common.inactive'), value: 'inactive' }] } },
];
