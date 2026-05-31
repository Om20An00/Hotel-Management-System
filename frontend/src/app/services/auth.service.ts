import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { ApiResponse } from '../models/models';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private base = 'http://localhost:8080/api/auth';

  constructor(private http: HttpClient) {}

  register(data: any): Observable<ApiResponse<any>> {
    return this.http.post<ApiResponse<any>>(`${this.base}/register`, data);
  }

  login(data: any): Observable<ApiResponse<any>> {
    return this.http.post<ApiResponse<any>>(`${this.base}/login`, data).pipe(
      tap(res => {
        if (res.success && res.data) {
          localStorage.setItem('token', res.data.token);
          localStorage.setItem('role', res.data.role);
          localStorage.setItem('username', res.data.username);
          localStorage.setItem('customerName', res.data.customerName);
          localStorage.setItem('userId', res.data.userId);
        }
      })
    );
  }

  logout(): void { localStorage.clear(); }
  getToken(): string | null { return localStorage.getItem('token'); }
  getRole(): string | null { return localStorage.getItem('role'); }
  getUsername(): string | null { return localStorage.getItem('username'); }
  getCustomerName(): string | null { return localStorage.getItem('customerName'); }
  isLoggedIn(): boolean { return !!this.getToken(); }
  isAdmin(): boolean { return this.getRole() === 'ADMIN'; }
  isStaff(): boolean { return this.getRole() === 'STAFF'; }
  isCustomer(): boolean { return this.getRole() === 'CUSTOMER'; }
}
