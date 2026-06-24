import { useI18n } from '@/hooks/web/useI18n';
const { t } = useI18n();
import { BasicColumn, FormSchema } from '@/components/Table';

export const columns: BasicColumn[] = [
  { title: t('fms.common.id'), dataIndex: 'id', ifShow: false },
  { title: t('fms.partners.code'), dataIndex: 'code', width: 120 },
  { title: t('fms.partners.partnerName'), dataIndex: 'name', width: 200 },
  { title: t('fms.customers.contactPerson'), dataIndex: 'contactPerson', width: 150 },
  { title: t('fms.common.phone'), dataIndex: 'contactPhone', width: 150 },
  { title: t('fms.common.status'), dataIndex: 'status', width: 120 },
];

export const searchFormSchema: FormSchema[] = [
  { field: 'name', label: t('fms.partners.partnerName'), component: 'Input', colProps: { span: 6 } },
  { field: 'code', label: t('fms.partners.partnerCode'), component: 'Input', colProps: { span: 6 } },
];

export const formSchema: FormSchema[] = [
  { field: 'id', label: t('fms.common.id'), component: 'Input', show: false },
  { field: 'code', label: t('fms.partners.code'), component: 'Input', required: true },
  { field: 'name', label: t('fms.partners.partnerName'), component: 'Input', required: true },
  { field: 'address', label: t('fms.common.address'), component: 'Input' },
  { field: 'contactPerson', label: t('fms.customers.contactPerson'), component: 'Input' },
  { field: 'contactPhone', label: t('fms.customers.contactPhone'), component: 'Input' },
  { field: 'status', label: t('fms.common.status'), component: 'Select', componentProps: { options: [{ label: t('fms.partners.active'), value: 'active' }, { label: t('fms.partners.inactive'), value: 'inactive' }] } },
];
