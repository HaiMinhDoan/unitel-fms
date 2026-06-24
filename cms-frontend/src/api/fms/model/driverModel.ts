import { FileAttachmentResponse } from './baseModel';

export interface DriverRequest {
  id?: string;
  code?: string;
  fullName?: string;
  dateOfBirth?: string;
  licenseNumber?: string;
  licenseClass?: string;
  licenseExpiry?: string;
  phone?: string;
  identityNumber?: string;
  status?: string;
  organizationId?: string;
}

export interface DriverResponse extends DriverRequest {
  createdAt?: string;
}

export interface DriverDocumentRequest {
  id?: string;
  driverId?: string;
  docType?: string;
  docNumber?: string;
  issueDate?: string;
  expiryDate?: string;
  status?: string;
  files?: File[];
}

export interface DriverDocumentResponse extends Omit<DriverDocumentRequest, 'files'> {
  createdAt?: string;
  updatedAt?: string;
  attachments?: FileAttachmentResponse[];
}
