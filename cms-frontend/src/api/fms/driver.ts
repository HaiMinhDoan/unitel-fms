import { defHttp } from '@/utils/http/axios';
import { DriverRequest, DriverResponse, DriverDocumentRequest, DriverDocumentResponse } from './model/driverModel';
import { BaseFilterRequest, Page } from './model/baseModel';

enum Api {
  Create = '/api/v1/driver/create',
  Update = '/api/v1/driver/update',
  Filter = '/api/v1/drivers/filter',
  CreateDocument = '/api/v1/driver-document/create',
  FilterDocuments = '/api/v1/driver-documents/filter',
}

export function createDriver(params: DriverRequest) {
  return defHttp.post<DriverResponse>({ url: Api.Create, params });
}

export function updateDriver(id: string, params: DriverRequest) {
  return defHttp.put<DriverResponse>({ url: `${Api.Update}/${id}`, params });
}

export function filterDrivers(params: BaseFilterRequest) {
  return defHttp.post<Page<DriverResponse>>({ url: Api.Filter, params });
}

export function createDriverDocument(params: DriverDocumentRequest) {
  const formData = new FormData();
  Object.keys(params).forEach((key) => {
    if (key === 'files' && params.files) {
      params.files.forEach((file) => formData.append('files', file));
    } else if (params[key] !== undefined && params[key] !== null && params[key] !== '') {
      formData.append(key, params[key]);
    }
  });
  return defHttp.post<DriverDocumentResponse>({
    url: Api.CreateDocument,
    params: formData,
    headers: { 'Content-Type': 'multipart/form-data' },
  });
}

export function updateDriverDocument(id: string, params: DriverDocumentRequest) {
  const formData = new FormData();
  Object.keys(params).forEach((key) => {
    if (key === 'files' && params.files) {
      params.files.forEach((file) => formData.append('files', file));
    } else if (params[key] !== undefined && params[key] !== null && params[key] !== '') {
      formData.append(key, params[key]);
    }
  });
  return defHttp.put<DriverDocumentResponse>({
    url: `/api/v1/driver-document/update/${id}`,
    params: formData,
    headers: { 'Content-Type': 'multipart/form-data' },
  });
}

export function changeDriverDocumentStatus(id: string, status: string) {
  return defHttp.patch<DriverDocumentResponse>({
    url: `/api/v1/driver-document/change-status/${id}`,
    params: { status },
  });
}

export function deleteDriverDocument(id: string) {
  return defHttp.delete<void>({ url: `/api/v1/driver-document/soft-delete/${id}` });
}

export function getExpiringDriverDocuments() {
  return defHttp.get<DriverDocumentResponse[]>({ url: `/api/v1/driver-documents/get-expiring-soon` });
}

export function filterDriverDocuments(params: BaseFilterRequest) {
  return defHttp.post<Page<DriverDocumentResponse>>({ url: Api.FilterDocuments, params });
}
