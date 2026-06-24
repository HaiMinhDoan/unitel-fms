import { defHttp } from '@/utils/http/axios';
import { UserRequest, UserResponse } from './model/userModel';
import { BaseFilterRequest, Page } from './model/baseModel';

enum Api {
  Create = '/api/v1/user/create',
  Update = '/api/v1/user/update',
  Filter = '/api/v1/users/filter',
  SoftDelete = '/api/v1/user/soft-delete',
  ChangeStatus = '/api/v1/user/change-status',
}

export function createUser(params: UserRequest) {
  return defHttp.post<UserResponse>({ url: Api.Create, params });
}

export function updateUser(id: string, params: UserRequest) {
  return defHttp.put<UserResponse>({ url: `${Api.Update}/${id}`, params });
}

export function filterUsers(params: BaseFilterRequest) {
  return defHttp.post<Page<UserResponse>>({ url: Api.Filter, params });
}

export function softDeleteUser(id: string) {
  return defHttp.delete({ url: `${Api.SoftDelete}/${id}` });
}

export function changeUserStatus(id: string, status: string) {
  return defHttp.patch({ url: `${Api.ChangeStatus}/${id}`, params: { status } });
}
