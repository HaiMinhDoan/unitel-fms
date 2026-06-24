export interface DispatchRequestDto {
  id?: string;
  reqNumber?: string;
  customerId?: string;
  pickupLocation?: string;
  dropoffLocation?: string;
  requiredVehicleTypeId?: string;
  requiredDate?: string;
  requiredCapacity?: number;
  status?: string;
  notes?: string;
}

export interface DispatchAssignmentDto {
  id?: string;
  dispatchRequestId?: string;
  assignmentType?: string;
  vehicleId?: string;
  driverId?: string;
  partnerId?: string;
  partnerVehicleId?: string;
  partnerDriverId?: string;
  scheduledStartTime?: string;
  status?: string;
}
