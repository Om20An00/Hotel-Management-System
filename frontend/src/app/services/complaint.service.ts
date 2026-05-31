import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse, Complaint, Page } from '../models/models';

@Injectable({ providedIn: 'root' })
export class ComplaintService {
  private base = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  submit(data: any): Observable<ApiResponse<Complaint>> {
    return this.http.post<ApiResponse<Complaint>>(`${this.base}/customer/complaints`, data);
  }

  getMine(): Observable<ApiResponse<Complaint[]>> {
    return this.http.get<ApiResponse<Complaint[]>>(`${this.base}/customer/complaints`);
  }

  getById(id: string): Observable<ApiResponse<Complaint>> {
    return this.http.get<ApiResponse<Complaint>>(`${this.base}/customer/complaints/${id}`);
  }

  confirmResolution(id: string): Observable<ApiResponse<Complaint>> {
    return this.http.put<ApiResponse<Complaint>>(`${this.base}/customer/complaints/${id}/confirm-resolution`, {});
  }

  reopen(id: string): Observable<ApiResponse<Complaint>> {
    return this.http.put<ApiResponse<Complaint>>(`${this.base}/customer/complaints/${id}/reopen`, {});
  }

  adminGetAll(page = 0, size = 10, query = ''): Observable<ApiResponse<Page<Complaint>>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (query) params = params.set('query', query);
    return this.http.get<ApiResponse<Page<Complaint>>>(`${this.base}/admin/complaints`, { params });
  }

  adminAssign(complaintId: string, staffId: number): Observable<ApiResponse<Complaint>> {
    return this.http.put<ApiResponse<Complaint>>(`${this.base}/admin/complaints/${complaintId}/assign`, { staffId });
  }

  adminUpdateStatus(complaintId: string, status: string, response: string, actionLog: string): Observable<ApiResponse<Complaint>> {
    return this.http.put<ApiResponse<Complaint>>(`${this.base}/admin/complaints/${complaintId}/status`, { status, response, actionLog });
  }

  staffGetMine(): Observable<ApiResponse<Complaint[]>> {
    return this.http.get<ApiResponse<Complaint[]>>(`${this.base}/staff/complaints`);
  }

  staffUpdateStatus(complaintId: string, status: string, response: string, actionLog: string): Observable<ApiResponse<Complaint>> {
    return this.http.put<ApiResponse<Complaint>>(`${this.base}/staff/complaints/${complaintId}/status`, { status, response, actionLog });
  }
}
