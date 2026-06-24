import { useI18n } from '@/hooks/web/useI18n';
const { t } = useI18n();
import { BasicColumn, FormSchema } from '@/components/Table';

export const columns: BasicColumn[] = [
  { title: t('fms.common.id'), dataIndex: 'id', ifShow: false },
  { title: t('fms.vehicles.licensePlate'), dataIndex: 'plateNumber' },
  { title: t('fms.vehicles.brand'), dataIndex: 'brand' },
  { title: t('fms.common.status'), dataIndex: 'status' },
  { 
    title: t('fms.vehicles.location'), 
    dataIndex: 'location',
    customRender: ({ record }) => {
      if (record.lastKnownLat && record.lastKnownLng) {
        return `${record.lastKnownLat}, ${record.lastKnownLng}`;
      }
      return 'Chưa có dữ liệu';
    }
  },
];

export const searchFormSchema: FormSchema[] = [
  { field: 'plateNumber', label: t('fms.vehicles.licensePlate'), component: 'Input', colProps: { span: 8 } },
  { field: 'brand', label: t('fms.vehicles.brand'), component: 'Input', colProps: { span: 8 } },
  { 
    field: 'vehicleTypeId', 
    label: t('fms.vehicles.vehicleType'), 
    component: 'ApiSelect', 
    colProps: { span: 8 },
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
  { 
    field: 'status', 
    label: t('fms.common.status'), 
    component: 'Select', 
    colProps: { span: 8 },
    componentProps: { 
      options: [
        { label: t('fms.common.active'), value: 'available' }, 
        { label: t('fms.vehicles.statusOptions.MAINTENANCE'), value: 'maintenance' }
      ] 
    } 
  },
];

import { filterVehicleTypes } from '@/api/fms/vehicle';

export const formSchema: FormSchema[] = [
  { field: 'id', label: t('fms.common.id'), component: 'Input', show: false },
  { field: 'plateNumber', label: t('fms.vehicles.licensePlate'), component: 'Input', required: true },
  { field: 'brand', label: t('fms.vehicles.brand'), component: 'Input' },
  { 
    field: 'vehicleTypeId', 
    label: t('fms.vehicles.vehicleType'), 
    component: 'ApiSelect', 
    required: true,
    componentProps: {
      api: async (params: any) => {
        const filterReq: any = {
            page: 0,
            size: 50,
            filters: []
        };
        if (params && params.nameEn) {
            filterReq.filters.push({
                fieldName: 'nameEn',
                operation: 'ILIKE',
                value: params.nameEn,
                logicType: 'OR'
            });
            filterReq.filters.push({
                fieldName: 'nameVi',
                operation: 'ILIKE',
                value: params.nameEn,
                logicType: 'OR'
            });
        }
        const res = await filterVehicleTypes(filterReq);
        return res.content;
      },
      labelField: 'nameEn',
      valueField: 'id',
      apiSearch: {
          show: true,
          searchName: 'nameEn',
      }
    }
  },
  { field: 'status', label: t('fms.common.status'), component: 'Select', componentProps: { options: [{ label: t('fms.common.active'), value: 'available' }, { label: t('fms.vehicles.statusOptions.MAINTENANCE'), value: 'maintenance' }] } },
  { field: 'lastKnownLat', label: t('fms.vehicles.lat'), component: 'InputNumber' },
  { field: 'lastKnownLng', label: t('fms.vehicles.lng'), component: 'InputNumber' },
];
