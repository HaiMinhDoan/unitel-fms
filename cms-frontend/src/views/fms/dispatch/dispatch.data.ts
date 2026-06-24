import { useI18n } from '@/hooks/web/useI18n';
const { t } = useI18n();
import { BasicColumn, FormSchema } from '@/components/Table';

export const columns: BasicColumn[] = [
  { title: t('fms.common.id'), dataIndex: 'id', ifShow: false },
  { title: t('fms.dispatch.requestCode'), dataIndex: 'reqNumber', width: 150 },
  { title: t('fms.dispatch.customer'), dataIndex: 'customerId', width: 150 },
  { title: t('fms.dispatch.pickupLocation'), dataIndex: 'pickupLocation', width: 200 },
  { title: t('fms.dispatch.dropoffLocation'), dataIndex: 'dropoffLocation', width: 200 },
  { title: t('fms.dispatch.requestTime'), dataIndex: 'requiredDate', width: 180 },
  { title: t('fms.common.status'), dataIndex: 'status', width: 120 },
];

export const searchFormSchema: FormSchema[] = [
  { field: 'reqNumber', label: t('fms.dispatch.requestCode'), component: 'Input', colProps: { span: 6 } },
  { 
    field: 'customerId', 
    label: t('fms.dispatch.customerId'), 
    component: 'ApiSelect', 
    colProps: { span: 6 },
    componentProps: {
      api: async (params: any) => {
        const filterReq: any = { page: 0, size: 50, filters: [] };
        if (params && params.name) {
            filterReq.filters.push({ fieldName: 'name', operation: 'ILIKE', value: params.name, logicType: 'AND' });
        }
        const res = await filterCustomers(filterReq);
        return res.content;
      },
      labelField: 'name',
      valueField: 'id',
      apiSearch: { show: true, searchName: 'name' }
    }
  },
  { field: 'pickupLocation', label: t('fms.dispatch.pickupLocation'), component: 'Input', colProps: { span: 6 } },
  { field: 'dropoffLocation', label: t('fms.dispatch.dropoffLocation'), component: 'Input', colProps: { span: 6 } },
  { field: 'status', label: t('fms.common.status'), component: 'Select', colProps: { span: 6 }, componentProps: { options: [{ label: t('fms.dispatch.statusOptions.DRAFT'), value: 'pending' }, { label: t('fms.dispatch.statusOptions.ASSIGNED'), value: 'assigned' }, { label: t('fms.dispatch.statusOptions.COMPLETED'), value: 'completed' }, { label: t('fms.dispatch.statusOptions.CANCELLED'), value: 'cancelled' }] } },
];

import { filterCustomers } from '@/api/fms/customer';
import { filterVehicleTypes } from '@/api/fms/vehicle';

export const formSchema: FormSchema[] = [
  { field: 'id', label: t('fms.common.id'), component: 'Input', show: false },
  { field: 'reqNumber', label: t('fms.dispatch.requestCode'), component: 'Input', required: true },
  { 
    field: 'customerId', 
    label: t('fms.dispatch.customerId'), 
    component: 'ApiSelect', 
    required: true,
    componentProps: {
      api: async (params: any) => {
        const filterReq: any = { page: 0, size: 50, filters: [] };
        if (params && params.name) {
            filterReq.filters.push({ fieldName: 'name', operation: 'ILIKE', value: params.name, logicType: 'AND' });
        }
        const res = await filterCustomers(filterReq);
        return res.content;
      },
      labelField: 'name',
      valueField: 'id',
      apiSearch: { show: true, searchName: 'name' }
    }
  },
  { field: 'pickupLocation', label: t('fms.dispatch.pickupLocation'), component: 'Input', required: true },
  { field: 'dropoffLocation', label: t('fms.dispatch.dropoffLocation'), component: 'Input', required: true },
  { 
    field: 'requiredVehicleTypeId', 
    label: t('fms.dispatch.vehicleType'), 
    component: 'ApiSelect',
    componentProps: {
      api: async (params: any) => {
        const filterReq: any = { page: 0, size: 50, filters: [] };
        if (params && params.nameEn) {
            filterReq.filters.push({ fieldName: 'nameEn', operation: 'ILIKE', value: params.nameEn, logicType: 'OR' });
            filterReq.filters.push({ fieldName: 'nameVi', operation: 'ILIKE', value: params.nameEn, logicType: 'OR' });
        }
        const res = await filterVehicleTypes(filterReq);
        return res.content;
      },
      labelField: 'nameEn',
      valueField: 'id',
      apiSearch: { show: true, searchName: 'nameEn' }
    }
  },
  { field: 'requiredCapacity', label: t('fms.dispatch.capacity'), component: 'InputNumber' },
  { field: 'requiredDate', label: t('fms.dispatch.pickupTime'), component: 'DatePicker', componentProps: { showTime: true, format: 'YYYY-MM-DD HH:mm:ss' } },
  { field: 'status', label: t('fms.common.status'), component: 'Select', componentProps: { options: [{ label: t('fms.dispatch.statusOptions.DRAFT'), value: 'pending' }, { label: t('fms.dispatch.statusOptions.ASSIGNED'), value: 'assigned' }, { label: t('fms.dispatch.statusOptions.COMPLETED'), value: 'completed' }, { label: t('fms.dispatch.statusOptions.CANCELLED'), value: 'cancelled' }] } },
  { field: 'notes', label: t('fms.common.note'), component: 'InputTextArea' },
];
