import { FileAttachmentResponse } from './baseModel';

export interface FuelLogRequest {
  id?: string;
  vehicleId?: string;
  driverId?: string;
  fuelType?: string;
  volumeLiters?: number;
  totalCost?: number;
  odometerReading?: number;
  location?: string;
  filledAt?: string;
  reportedConsumption?: number;
  telemetryConsumption?: number;
  anomalyFlagged?: boolean;
  anomalyNotes?: string;
  files?: File[];
}

export interface FuelLogResponse extends Omit<FuelLogRequest, 'files'> {
  createdAt?: string;
  updatedAt?: string;
  attachments?: FileAttachmentResponse[];
}

export interface MaintenanceOrderDto {
  id?: string;
  orderNumber?: string;
  vehicleId?: string;
  requestedBy?: string;
  scheduledDate?: string;
  actualDate?: string;
  totalCost?: number;
  status?: string;
  notes?: string;
}
