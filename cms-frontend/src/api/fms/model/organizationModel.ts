export interface OrganizationRequest {
  id?: string;
  name?: string;
  code?: string;
  type?: string;
  parentId?: string;
  contactEmail?: string;
  contactPhone?: string;
  status?: string;
}

export interface OrganizationResponse extends OrganizationRequest {
  createdAt?: string;
}
