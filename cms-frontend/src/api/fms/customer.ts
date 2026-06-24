import { defHttp } from '@/utils/http/axios';
import { CustomerRequest, CustomerResponse } from './model/customerModel';
import { BaseFilterRequest, Page } from './model/baseModel';

enum Api {
  Create = '/api/v1/customer/create',
  Update = '/api/v1/customer/update',
  Filter = '/api/v1/customers/filter',
}

export function createCustomer(params: CustomerRequest) {
  return defHttp.post<CustomerResponse>({ url: Api.Create, params });
}

export function updateCustomer(id: string, params: CustomerRequest) {
  return defHttp.put<CustomerResponse>({ url: `${Api.Update}/${id}`, params });
}

export function filterCustomers(params: BaseFilterRequest) {
  return defHttp.post<Page<CustomerResponse>>({ url: Api.Filter, params });
}
