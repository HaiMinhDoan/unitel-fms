export interface PartnerRequest {
  id?: string;
  code?: string;
  name?: string;
  taxCode?: string;
  address?: string;
  contactPerson?: string;
  contactPhone?: string;
  status?: string;
}

export interface PartnerResponse extends PartnerRequest {
  createdAt?: string;
}
