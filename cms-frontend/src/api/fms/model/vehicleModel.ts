import { FileAttachmentResponse } from './baseModel';

export interface VehicleRequest {
  id?: string;
  plateNumber?: string;
  vin?: string;
  engineNumber?: string;
  brand?: string;
  model?: string;
  vehicleTypeId?: string;
  yearOfManufacture?: number;
  currentOdometer?: number;
  status?: string;
  ownershipType?: string;
  partnerId?: string;
}

export interface VehicleResponse extends VehicleRequest {
  lastKnownLat?: number;
  lastKnownLng?: number;
  statusChangedAt?: string;
  createdAt?: string;
}

export interface VehicleDocumentRequest {
  id?: string;
  vehicleId?: string;
  docType?: string;
  docNumber?: string;
  issueDate?: string;
  expiryDate?: string;
  status?: string;
  files?: File[];
}

export interface VehicleDocumentResponse extends Omit<VehicleDocumentRequest, 'files'> {
  createdAt?: string;
  updatedAt?: string;
  attachments?: FileAttachmentResponse[];
}

export interface VehicleTypeResponse {
  id?: string;
  code?: string;
  nameEn?: string;
  nameVi?: string;
}
