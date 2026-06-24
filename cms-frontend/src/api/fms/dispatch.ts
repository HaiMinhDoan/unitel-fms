import { defHttp } from '@/utils/http/axios';
import { DispatchRequestDto, DispatchAssignmentDto } from './model/dispatchModel';
import { BaseFilterRequest, Page } from './model/baseModel';

enum Api {
  DispatchRequests = '/api/v1/dispatch-requests',
  DispatchAssignments = '/api/v1/dispatch-assignments',
}

export function filterDispatchRequests(params: BaseFilterRequest) {
  // Assuming the POST to the base path acts as the filter based on the prompt or code
  return defHttp.post<Page<DispatchRequestDto>>({ url: `${Api.DispatchRequests}/filter`, params });
}

export function filterDispatchAssignments(params: BaseFilterRequest) {
  return defHttp.post<Page<DispatchAssignmentDto>>({ url: `${Api.DispatchAssignments}/filter`, params });
}

export function createDispatchRequest(params: DispatchRequestDto) {
  return defHttp.post<DispatchRequestDto>({ url: Api.DispatchRequests, params });
}

export function createDispatchAssignment(params: DispatchAssignmentDto) {
  return defHttp.post<DispatchAssignmentDto>({ url: Api.DispatchAssignments, params });
}

export function updateDispatchRequest(id: string, params: DispatchRequestDto) {
  return defHttp.put<DispatchRequestDto>({ url: `${Api.DispatchRequests}/${id}`, params });
}
