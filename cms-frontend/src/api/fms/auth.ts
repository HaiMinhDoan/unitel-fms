import { defHttp } from '@/utils/http/axios';
import { LoginRequest, AuthResponse } from './model/authModel';

enum Api {
  Login = '/api/v1/auth/login',
  Refresh = '/api/v1/auth/refresh',
  Logout = '/api/v1/auth/logout',
  GetUserInfo = '/api/v1/user/me',
}

export function loginApi(params: LoginRequest) {
  return defHttp.post<AuthResponse>({ url: Api.Login, params });
}

export function refreshApi(refreshToken: string) {
  return defHttp.post<AuthResponse>({ url: Api.Refresh, params: { refreshToken } });
}

export function getUserInfo() {
  return defHttp.get<any>({ url: Api.GetUserInfo });
}

export function doLogout() {
  return defHttp.post({ url: Api.Logout });
}
