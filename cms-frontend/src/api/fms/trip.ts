import { defHttp } from '@/utils/http/axios';
import { TripDto, TripStopDto, TripIncidentRequest, TripIncidentResponse, EpodRequest } from './model/tripModel';
import { BaseFilterRequest, Page } from './model/baseModel';

enum Api {
  Trips = '/api/v1/trips',
  TripStops = '/api/v1/trip-stops',
  TripIncidents = '/api/v1/trip-incidents',
}

export function filterTrips(params: BaseFilterRequest) {
  return defHttp.post<Page<TripDto>>({ url: `${Api.Trips}/filter`, params });
}

export function filterTripStops(params: BaseFilterRequest) {
  return defHttp.post<Page<TripStopDto>>({ url: `${Api.TripStops}/filter`, params });
}

export function createTripIncident(params: TripIncidentRequest) {
  const formData = new FormData();
  Object.keys(params).forEach((key) => {
    if (key === 'files' && params.files) {
      params.files.forEach((file) => formData.append('files', file));
    } else if (params[key] !== undefined && params[key] !== null && params[key] !== '') {
      formData.append(key, params[key]);
    }
  });
  return defHttp.post<TripIncidentResponse>({
    url: Api.TripIncidents,
    params: formData,
    headers: { 'Content-Type': 'multipart/form-data' },
  });
}

export function filterTripIncidents(params: BaseFilterRequest) {
  return defHttp.post<Page<TripIncidentResponse>>({ url: `${Api.TripIncidents}/filter`, params });
}

export function resolveTripIncident(id: string, resolutionNotes: string) {
  return defHttp.post<TripIncidentResponse>({
    url: `${Api.TripIncidents}/${id}/resolve`,
    params: { resolutionNotes },
  });
}

export function submitTripStopEpod(id: string, params: EpodRequest) {
  const formData = new FormData();
  if (params.photo) {
    formData.append('photo', params.photo);
  }
  if (params.signature) {
    formData.append('signature', params.signature);
  }
  return defHttp.post<TripStopDto>({
    url: `${Api.TripStops}/${id}/epod`,
    params: formData,
    headers: { 'Content-Type': 'multipart/form-data' },
  });
}
