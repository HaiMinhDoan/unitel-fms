export interface CustomerRequest {
  id?: string;
  code?: string;
  name?: string;
  taxCode?: string;
  address?: string;
  contactPerson?: string;
  contactPhone?: string;
  status?: string;
}

export interface CustomerResponse extends CustomerRequest {
  createdAt?: string;
}
