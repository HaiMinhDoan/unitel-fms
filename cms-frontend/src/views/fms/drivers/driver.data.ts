import { useI18n } from '@/hooks/web/useI18n';
const { t } = useI18n();
import { BasicColumn, FormSchema } from '@/components/Table';

export const columns: BasicColumn[] = [
  { title: t('fms.common.id'), dataIndex: 'id', ifShow: false },
  { title: t('fms.drivers.driverCode'), dataIndex: 'code', width: 120 },
  { title: t('fms.drivers.fullName'), dataIndex: 'fullName', width: 180 },
  { title: t('fms.common.phone'), dataIndex: 'phone', width: 150 },
  { title: t('fms.drivers.licenseNumber'), dataIndex: 'licenseNumber', width: 150 },
  { title: t('fms.drivers.licenseClass'), dataIndex: 'licenseClass', width: 100 },
  { title: t('fms.common.status'), dataIndex: 'status', width: 120 },
];

export const searchFormSchema: FormSchema[] = [
  { field: 'fullName', label: t('fms.drivers.fullName'), component: 'Input', colProps: { span: 6 } },
  { field: 'licenseNumber', label: t('fms.drivers.licenseNumber'), component: 'Input', colProps: { span: 6 } },
  { field: 'licenseClass', label: t('fms.drivers.licenseClass'), component: 'Input', colProps: { span: 6 } },
  { field: 'status', label: t('fms.common.status'), component: 'Select', colProps: { span: 6 }, componentProps: { options: [{ label: t('fms.drivers.statusOptions.AVAILABLE'), value: 'available' }, { label: t('fms.dispatchAssignments.running'), value: 'on_trip' }, { label: t('fms.drivers.statusOptions.INACTIVE'), value: 'inactive' }] } },
];

export const formSchema: FormSchema[] = [
  { field: 'id', label: t('fms.common.id'), component: 'Input', show: false },
  { field: 'code', label: t('fms.drivers.driverCode'), component: 'Input', required: true },
  { field: 'fullName', label: t('fms.drivers.fullName'), component: 'Input', required: true },
  { field: 'phone', label: t('fms.common.phone'), component: 'Input' },
  { field: 'licenseNumber', label: t('fms.drivers.licenseNumber'), component: 'Input', required: true },
  { field: 'licenseClass', label: t('fms.drivers.licenseClass'), component: 'Input' },
  { field: 'status', label: t('fms.common.status'), component: 'Select', componentProps: { options: [{ label: t('fms.drivers.statusOptions.AVAILABLE'), value: 'available' }, { label: t('fms.dispatchAssignments.running'), value: 'on_trip' }, { label: t('fms.drivers.statusOptions.INACTIVE'), value: 'inactive' }] } },
];
