import { BaseFilterRequest, Page } from './baseModel';

export interface UserRequest {
  id?: string;
  username?: string;
  email?: string;
  phone?: string;
  status?: string;
  orgId?: string;
}

export interface UserResponse {
  id: string;
  username: string;
  email: string;
  phone: string;
  status: string;
  lastLoginAt: string;
  createdAt: string;
}
