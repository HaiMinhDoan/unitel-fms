import { useI18n } from '@/hooks/web/useI18n';
const { t } = useI18n();
import { BasicColumn, FormSchema } from '@/components/Table';

export const columns: BasicColumn[] = [
  { title: t('fms.common.id'), dataIndex: 'id', ifShow: false },
  { title: t('fms.maintenance.orderNumber'), dataIndex: 'orderNumber', width: 150 },
  { title: t('fms.maintenance.vehicle'), dataIndex: 'vehicleId', width: 150 },
  { title: t('fms.maintenance.date'), dataIndex: 'scheduledDate', width: 180 },
  { title: t('fms.maintenance.completedDate'), dataIndex: 'actualDate', width: 180 },
  { title: t('fms.maintenance.cost'), dataIndex: 'totalCost', width: 150 },
  { title: t('fms.common.status'), dataIndex: 'status', width: 120 },
];

export const searchFormSchema: FormSchema[] = [
  { field: 'orderNumber', label: t('fms.maintenance.orderNumber'), component: 'Input', colProps: { span: 6 } },
  { field: 'vehicleId', label: t('fms.maintenance.vehicle'), component: 'Input', colProps: { span: 6 } },
];

import { filterVehicles } from '@/api/fms/vehicle';

export const formSchema: FormSchema[] = [
  { field: 'id', label: t('fms.common.id'), component: 'Input', show: false },
  { field: 'orderNumber', label: t('fms.maintenance.orderNumber'), component: 'Input', required: true },
  { 
    field: 'vehicleId', 
    label: t('fms.cost.vehicleId'), 
    component: 'ApiSelect', 
    required: true,
    componentProps: {
      api: async (params: any) => {
        const filterReq: any = { page: 0, size: 50, filters: [] };
        if (params && params.plateNumber) {
            filterReq.filters.push({ fieldName: 'plateNumber', operation: 'ILIKE', value: params.plateNumber, logicType: 'AND' });
        }
        const res = await filterVehicles(filterReq);
        return res.content;
      },
      labelField: 'plateNumber',
      valueField: 'id',
      apiSearch: { show: true, searchName: 'plateNumber' }
    }
  },
  { field: 'requestedBy', label: t('fms.maintenance.requester'), component: 'Input' },
  { field: 'scheduledDate', label: t('fms.maintenance.date'), component: 'DatePicker', componentProps: { format: 'YYYY-MM-DD' } },
  { field: 'actualDate', label: t('fms.maintenance.completedDate'), component: 'DatePicker', componentProps: { format: 'YYYY-MM-DD' } },
  { field: 'totalCost', label: t('fms.maintenance.cost'), component: 'InputNumber' },
  { field: 'status', label: t('fms.common.status'), component: 'Select', componentProps: { options: [{ label: t('fms.maintenance.statusOptions.SCHEDULED'), value: 'pending' }, { label: t('fms.dispatch.statusOptions.COMPLETED'), value: 'completed' }] } },
  { field: 'notes', label: t('fms.common.note'), component: 'InputTextArea' },
];
