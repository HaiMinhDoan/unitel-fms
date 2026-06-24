import { defHttp } from '@/utils/http/axios';
import { PartnerRequest, PartnerResponse } from './model/partnerModel';
import { BaseFilterRequest, Page } from './model/baseModel';

enum Api {
  Create = '/api/v1/partner/create',
  Update = '/api/v1/partner/update',
  Filter = '/api/v1/partners/filter',
  FilterVehicles = '/api/v1/partner-vehicles/filter',
  FilterDrivers = '/api/v1/partner-drivers/filter',
}

export function createPartner(params: PartnerRequest) {
  return defHttp.post<PartnerResponse>({ url: Api.Create, params });
}

export function updatePartner(id: string, params: PartnerRequest) {
  return defHttp.put<PartnerResponse>({ url: `${Api.Update}/${id}`, params });
}

export function filterPartners(params: BaseFilterRequest) {
  return defHttp.post<Page<PartnerResponse>>({ url: Api.Filter, params });
}

export function filterPartnerVehicles(params: BaseFilterRequest) {
  // reusing Page<any> since we haven't imported VehicleResponse here
  return defHttp.post<Page<any>>({ url: Api.FilterVehicles, params });
}

export function filterPartnerDrivers(params: BaseFilterRequest) {
  return defHttp.post<Page<any>>({ url: Api.FilterDrivers, params });
}
