import { useI18n } from '@/hooks/web/useI18n';
const { t } = useI18n();
import { BasicColumn, FormSchema } from '@/components/Table';

export const columns: BasicColumn[] = [
  { title: t('fms.common.id'), dataIndex: 'id', ifShow: false },
  { title: t('fms.organizations.orgCode'), dataIndex: 'code', width: 120 },
  { title: t('fms.organizations.orgName'), dataIndex: 'name', width: 250 },
  { title: t('fms.organizations.description'), dataIndex: 'description', width: 300 },
];

export const searchFormSchema: FormSchema[] = [
  { field: 'name', label: t('fms.organizations.orgName'), component: 'Input', colProps: { span: 6 } },
];

export const formSchema: FormSchema[] = [
  { field: 'id', label: t('fms.common.id'), component: 'Input', show: false },
  { field: 'code', label: t('fms.organizations.orgCode'), component: 'Input', required: true },
  { field: 'name', label: t('fms.organizations.orgName'), component: 'Input', required: true },
  { field: 'parentId', label: t('fms.organizations.parent'), component: 'Input' },
  { field: 'description', label: t('fms.organizations.description'), component: 'InputTextArea' },
];
