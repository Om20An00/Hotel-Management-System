import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse, User, Page } from '../models/models';

@Injectable({ providedIn: 'root' })
export class UserService {
  private base = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  getProfile(): Observable<ApiResponse<User>> {
    return this.http.get<ApiResponse<User>>(`${this.base}/customer/profile`);
  }

  updateProfile(data: any): Observable<ApiResponse<User>> {
    return this.http.put<ApiResponse<User>>(`${this.base}/customer/profile`, data);
  }

  adminGetAll(page = 0, size = 10, query = ''): Observable<ApiResponse<Page<User>>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (query) params = params.set('query', query);
    return this.http.get<ApiResponse<Page<User>>>(`${this.base}/admin/users`, { params });
  }

  adminCreate(data: any): Observable<ApiResponse<User>> {
    return this.http.post<ApiResponse<User>>(`${this.base}/admin/users`, data);
  }

  adminUpdate(id: number, data: any): Observable<ApiResponse<User>> {
    return this.http.put<ApiResponse<User>>(`${this.base}/admin/users/${id}`, data);
  }

  adminToggleStatus(id: number): Observable<ApiResponse<User>> {
    return this.http.put<ApiResponse<User>>(`${this.base}/admin/users/${id}/toggle-status`, {});
  }

  adminResetPassword(id: number): Observable<ApiResponse<any>> {
    return this.http.put<ApiResponse<any>>(`${this.base}/admin/users/${id}/reset-password`, {});
  }

  adminGetStaff(): Observable<ApiResponse<Page<User>>> {
    return this.http.get<ApiResponse<Page<User>>>(`${this.base}/admin/staff`);
  }
}
