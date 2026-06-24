import { FileAttachmentResponse } from './baseModel';

export interface TripDto {
  id?: string;
  dispatchAssignmentId?: string;
  plannedStartAt?: string;
  plannedEndAt?: string;
  actualStartAt?: string;
  actualEndAt?: string;
  eta?: string;
  slaAtRisk?: boolean;
  totalDistanceKm?: number;
  status?: string;
}

export interface TripStopDto {
  id?: string;
  tripId?: string;
  stopSequence?: number;
  locationName?: string;
  lat?: number;
  lng?: number;
  plannedArrival?: string;
  actualArrival?: string;
}

export interface TripIncidentRequest {
  id?: string;
  tripId?: string;
  incidentType?: string;
  description?: string;
  severity?: string;
  reportedBy?: string;
  reportedAt?: string;
  status?: string;
  resolutionNotes?: string;
  resolvedBy?: string;
  resolvedAt?: string;
  files?: File[];
}

export interface TripIncidentResponse extends Omit<TripIncidentRequest, 'files'> {
  createdAt?: string;
  updatedAt?: string;
  attachments?: FileAttachmentResponse[];
}

export interface EpodRequest {
  photo?: File;
  signature?: File;
}
