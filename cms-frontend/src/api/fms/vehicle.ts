import { defHttp } from '@/utils/http/axios';
import { VehicleRequest, VehicleResponse, VehicleDocumentRequest, VehicleDocumentResponse, VehicleTypeResponse } from './model/vehicleModel';
import { BaseFilterRequest, Page } from './model/baseModel';

enum Api {
  Create = '/api/v1/vehicle/create',
  Update = '/api/v1/vehicle/update',
  Filter = '/api/v1/vehicles/filter',
  CreateDocument = '/api/v1/vehicle-document/create',
  FilterDocuments = '/api/v1/vehicle-documents/filter',
}

export function createVehicle(params: VehicleRequest) {
  return defHttp.post<VehicleResponse>({ url: Api.Create, params });
}

export function updateVehicle(id: string, params: VehicleRequest) {
  return defHttp.put<VehicleResponse>({ url: `${Api.Update}/${id}`, params });
}

export function filterVehicles(params: BaseFilterRequest) {
  return defHttp.post<Page<VehicleResponse>>({ url: Api.Filter, params });
}

export function createVehicleDocument(params: VehicleDocumentRequest) {
  const formData = new FormData();
  Object.keys(params).forEach((key) => {
    if (key === 'files' && params.files) {
      params.files.forEach((file) => formData.append('files', file));
    } else if (params[key] !== undefined && params[key] !== null && params[key] !== '') {
      formData.append(key, params[key]);
    }
  });
  return defHttp.post<VehicleDocumentResponse>({
    url: Api.CreateDocument,
    params: formData,
    headers: { 'Content-Type': 'multipart/form-data' },
  });
}

export function updateVehicleDocument(id: string, params: VehicleDocumentRequest) {
  const formData = new FormData();
  Object.keys(params).forEach((key) => {
    if (key === 'files' && params.files) {
      params.files.forEach((file) => formData.append('files', file));
    } else if (params[key] !== undefined && params[key] !== null && params[key] !== '') {
      formData.append(key, params[key]);
    }
  });
  return defHttp.put<VehicleDocumentResponse>({
    url: `/api/v1/vehicle-document/update/${id}`,
    params: formData,
    headers: { 'Content-Type': 'multipart/form-data' },
  });
}

export function changeVehicleDocumentStatus(id: string, status: string) {
  return defHttp.patch<VehicleDocumentResponse>({
    url: `/api/v1/vehicle-document/change-status/${id}`,
    params: { status },
  });
}

export function deleteVehicleDocument(id: string) {
  return defHttp.delete<void>({ url: `/api/v1/vehicle-document/soft-delete/${id}` });
}

export function getExpiringVehicleDocuments() {
  return defHttp.get<VehicleDocumentResponse[]>({ url: `/api/v1/vehicle-documents/get-expiring-soon` });
}

export function filterVehicleDocuments(params: BaseFilterRequest) {
  return defHttp.post<Page<VehicleDocumentResponse>>({ url: Api.FilterDocuments, params });
}

export function filterVehicleTypes(params: BaseFilterRequest) {
  return defHttp.post<Page<VehicleTypeResponse>>({ url: '/api/v1/vehicle-types/filter', params });
}

export function createVehicleType(params: VehicleTypeResponse) {
  return defHttp.post<VehicleTypeResponse>({ url: '/api/v1/vehicle-type/create', params });
}

export function updateVehicleType(id: string, params: VehicleTypeResponse) {
  return defHttp.put<VehicleTypeResponse>({ url: `/api/v1/vehicle-type/update/${id}`, params });
}