import { defHttp } from '@/utils/http/axios';
import { OrganizationRequest, OrganizationResponse } from './model/organizationModel';
import { BaseFilterRequest, Page } from './model/baseModel';

enum Api {
  Create = '/api/v1/organization/create',
  Update = '/api/v1/organization/update',
  Filter = '/api/v1/organizations/filter',
}

export function createOrganization(params: OrganizationRequest) {
  return defHttp.post<OrganizationResponse>({ url: Api.Create, params });
}

export function updateOrganization(id: string, params: OrganizationRequest) {
  return defHttp.put<OrganizationResponse>({ url: `${Api.Update}/${id}`, params });
}

export function filterOrganizations(params: BaseFilterRequest) {
  return defHttp.post<Page<OrganizationResponse>>({ url: Api.Filter, params });
}
