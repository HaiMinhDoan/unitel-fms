export interface ResponseData<T> {
  status: number;
  messageCode: string;
  data: T;
  error: string | null;
  path: string;
  timestamp: string;
}

export interface BaseFilterRequest {
  page: number;
  size: number;
  sorts?: Array<{ fieldName: string; ascending: boolean }>;
  filters?: Array<{
    fieldName: string;
    operation: 'EQUALS' | 'LESS_THAN' | 'LESS_THAN_OR_EQUAL' | 'GREATER_THAN' | 'LIKE' | 'NOT_LIKE' | 'ILIKE' | 'NOT_ILIKE' | 'IN' | 'NOT_IN';
    value: any;
  }>;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

export interface FileAttachmentResponse {
  id: string;
  bucket?: string;
  objectKey?: string;
  originalName?: string;
  mimeType?: string;
  extension?: string;
  sizeBytes?: number;
  entityType?: string;
  entityId?: string;
  uploadedBy?: string;
  status?: string;
  createdAt?: string;
  updatedAt?: string;
  publicUrl?: string;
}
