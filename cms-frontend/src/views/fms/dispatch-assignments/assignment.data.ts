import { useI18n } from '@/hooks/web/useI18n';
const { t } = useI18n();
import { BasicColumn, FormSchema } from '@/components/Table';

export const columns: BasicColumn[] = [
  { title: t('fms.common.id'), dataIndex: 'id', ifShow: false },
  { title: t('fms.dispatchAssignments.request'), dataIndex: 'dispatchRequestId', width: 150 },
  { title: t('fms.dispatchAssignments.type'), dataIndex: 'assignmentType', width: 150 },
  { title: t('fms.dispatchAssignments.internalVehicle'), dataIndex: 'vehicleId', width: 150 },
  { title: t('fms.dispatchAssignments.internalDriver'), dataIndex: 'driverId', width: 150 },
  { title: t('fms.dispatchAssignments.partner'), dataIndex: 'partnerId', width: 150 },
  { title: t('fms.dispatchAssignments.assignedAt'), dataIndex: 'scheduledStartTime', width: 180 },
  { title: t('fms.common.status'), dataIndex: 'status', width: 120 },
];

export const searchFormSchema: FormSchema[] = [
  { 
    field: 'dispatchRequestId', 
    label: t('fms.dispatchAssignments.request'), 
    component: 'ApiSelect', 
    colProps: { span: 6 },
    componentProps: {
      api: async (params: any) => {
        const filterReq: any = { page: 0, size: 50, filters: [] };
        if (params && params.reqNumber) {
            filterReq.filters.push({ fieldName: 'reqNumber', operation: 'ILIKE', value: params.reqNumber, logicType: 'AND' });
        }
        const res = await filterDispatchRequests(filterReq);
        return res.content;
      },
      labelField: 'reqNumber',
      valueField: 'id',
      apiSearch: { show: true, searchName: 'reqNumber' }
    }
  },
  { field: 'assignmentType', label: t('fms.dispatchAssignments.type'), component: 'Select', colProps: { span: 6 }, componentProps: { options: [{ label: t('fms.dispatchAssignments.internal'), value: 'internal' }, { label: t('fms.dispatchAssignments.external'), value: 'partner' }] } },
  { field: 'status', label: t('fms.common.status'), component: 'Select', colProps: { span: 6 }, componentProps: { options: [{ label: t('fms.dispatchAssignments.new'), value: 'assigned' }, { label: t('fms.dispatchAssignments.running'), value: 'in_progress' }, { label: t('fms.dispatch.statusOptions.COMPLETED'), value: 'completed' }] } },
  { 
    field: 'vehicleId', 
    label: t('fms.dispatchAssignments.internalVehicle'), 
    component: 'ApiSelect',
    colProps: { span: 6 },
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
  { 
    field: 'driverId', 
    label: t('fms.dispatchAssignments.internalDriver'), 
    component: 'ApiSelect',
    colProps: { span: 6 },
    componentProps: {
      api: async (params: any) => {
        const filterReq: any = { page: 0, size: 50, filters: [] };
        if (params && params.fullName) {
            filterReq.filters.push({ fieldName: 'fullName', operation: 'ILIKE', value: params.fullName, logicType: 'AND' });
        }
        const res = await filterDrivers(filterReq);
        return res.content;
      },
      labelField: 'fullName',
      valueField: 'id',
      apiSearch: { show: true, searchName: 'fullName' }
    }
  },
];

import { filterDispatchRequests } from '@/api/fms/dispatch';
import { filterVehicles } from '@/api/fms/vehicle';
import { filterDrivers } from '@/api/fms/driver';
import { filterPartners, filterPartnerVehicles, filterPartnerDrivers } from '@/api/fms/partner';

export const formSchema: FormSchema[] = [
  { field: 'id', label: t('fms.common.id'), component: 'Input', show: false },
  { 
    field: 'dispatchRequestId', 
    label: t('fms.dispatchAssignments.request'), 
    component: 'ApiSelect', 
    required: true,
    componentProps: {
      api: async (params: any) => {
        const filterReq: any = { page: 0, size: 50, filters: [] };
        if (params && params.reqNumber) {
            filterReq.filters.push({ fieldName: 'reqNumber', operation: 'ILIKE', value: params.reqNumber, logicType: 'AND' });
        }
        const res = await filterDispatchRequests(filterReq);
        return res.content;
      },
      labelField: 'reqNumber',
      valueField: 'id',
      apiSearch: { show: true, searchName: 'reqNumber' }
    }
  },
  { field: 'assignmentType', label: t('fms.dispatchAssignments.type'), component: 'Select', componentProps: { options: [{ label: t('fms.dispatchAssignments.internal'), value: 'internal' }, { label: t('fms.dispatchAssignments.external'), value: 'partner' }] }, required: true },
  { 
    field: 'vehicleId', 
    label: t('fms.dispatchAssignments.internalVehicle'), 
    component: 'ApiSelect',
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
  { 
    field: 'driverId', 
    label: t('fms.dispatchAssignments.internalDriver'), 
    component: 'ApiSelect',
    componentProps: {
      api: async (params: any) => {
        const filterReq: any = { page: 0, size: 50, filters: [] };
        if (params && params.fullName) {
            filterReq.filters.push({ fieldName: 'fullName', operation: 'ILIKE', value: params.fullName, logicType: 'AND' });
        }
        const res = await filterDrivers(filterReq);
        return res.content;
      },
      labelField: 'fullName',
      valueField: 'id',
      apiSearch: { show: true, searchName: 'fullName' }
    }
  },
  { 
    field: 'partnerId', 
    label: t('fms.dispatchAssignments.partner'), 
    component: 'ApiSelect',
    componentProps: {
      api: async (params: any) => {
        const filterReq: any = { page: 0, size: 50, filters: [] };
        if (params && params.name) {
            filterReq.filters.push({ fieldName: 'name', operation: 'ILIKE', value: params.name, logicType: 'AND' });
        }
        const res = await filterPartners(filterReq);
        return res.content;
      },
      labelField: 'name',
      valueField: 'id',
      apiSearch: { show: true, searchName: 'name' }
    }
  },
  { 
    field: 'partnerVehicleId', 
    label: t('fms.dispatchAssignments.externalVehicle'), 
    component: 'ApiSelect',
    componentProps: {
      api: async (params: any) => {
        const filterReq: any = { page: 0, size: 50, filters: [] };
        if (params && params.plateNumber) {
            filterReq.filters.push({ fieldName: 'plateNumber', operation: 'ILIKE', value: params.plateNumber, logicType: 'AND' });
        }
        const res = await filterPartnerVehicles(filterReq);
        return res.content;
      },
      labelField: 'plateNumber',
      valueField: 'id',
      apiSearch: { show: true, searchName: 'plateNumber' }
    }
  },
  { 
    field: 'partnerDriverId', 
    label: t('fms.dispatchAssignments.externalDriver'), 
    component: 'ApiSelect',
    componentProps: {
      api: async (params: any) => {
        const filterReq: any = { page: 0, size: 50, filters: [] };
        if (params && params.fullName) {
            filterReq.filters.push({ fieldName: 'fullName', operation: 'ILIKE', value: params.fullName, logicType: 'AND' });
        }
        const res = await filterPartnerDrivers(filterReq);
        return res.content;
      },
      labelField: 'fullName',
      valueField: 'id',
      apiSearch: { show: true, searchName: 'fullName' }
    }
  },
  { field: 'scheduledStartTime', label: t('fms.dispatchAssignments.assignedAt'), component: 'DatePicker', componentProps: { showTime: true, format: 'YYYY-MM-DD HH:mm:ss' } },
  { field: 'status', label: t('fms.common.status'), component: 'Select', componentProps: { options: [{ label: t('fms.dispatchAssignments.new'), value: 'assigned' }, { label: t('fms.dispatchAssignments.running'), value: 'in_progress' }, { label: t('fms.dispatch.statusOptions.COMPLETED'), value: 'completed' }] } },
];
