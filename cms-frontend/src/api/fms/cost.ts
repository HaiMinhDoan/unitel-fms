import { defHttp } from '@/utils/http/axios';
import { FuelLogRequest, FuelLogResponse, MaintenanceOrderDto } from './model/costModel';
import { BaseFilterRequest, Page } from './model/baseModel';

enum Api {
  FuelLogs = '/api/v1/fuel-logs',
  MaintenanceOrders = '/api/v1/maintenance-orders',
}

export function filterFuelLogs(params: BaseFilterRequest) {
  return defHttp.post<Page<FuelLogResponse>>({ url: `${Api.FuelLogs}/filter`, params });
}

export function createFuelLog(params: FuelLogRequest) {
  const formData = new FormData();
  Object.keys(params).forEach((key) => {
    if (key === 'files' && params.files) {
      params.files.forEach((file) => formData.append('files', file));
    } else if (params[key] !== undefined && params[key] !== null && params[key] !== '') {
      formData.append(key, params[key]);
    }
  });
  return defHttp.post<FuelLogResponse>({
    url: Api.FuelLogs,
    params: formData,
    headers: { 'Content-Type': 'multipart/form-data' },
  });
}

export function filterMaintenanceOrders(params: BaseFilterRequest) {
  return defHttp.post<Page<MaintenanceOrderDto>>({ url: `${Api.MaintenanceOrders}/filter`, params });
}

export function createMaintenanceOrder(params: MaintenanceOrderDto) {
  return defHttp.post<MaintenanceOrderDto>({ url: '/api/v1/maintenance-order/create', params });
}
