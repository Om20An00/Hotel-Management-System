import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse, Bill, Page } from '../models/models';

@Injectable({ providedIn: 'root' })
export class BillService {
  private base = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  adminGetAll(page = 0, size = 10, query = ''): Observable<ApiResponse<Page<Bill>>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (query) params = params.set('query', query);
    return this.http.get<ApiResponse<Page<Bill>>>(`${this.base}/admin/bills`, { params });
  }

  adminGetById(billId: string): Observable<ApiResponse<Bill>> {
    return this.http.get<ApiResponse<Bill>>(`${this.base}/admin/bills/${billId}`);
  }

  adminCreate(data: any): Observable<ApiResponse<Bill>> {
    return this.http.post<ApiResponse<Bill>>(`${this.base}/admin/bills`, data);
  }

  adminUpdate(billId: string, data: any): Observable<ApiResponse<Bill>> {
    return this.http.put<ApiResponse<Bill>>(`${this.base}/admin/bills/${billId}`, data);
  }

  downloadInvoice(bookingId: string): Observable<Blob> {
    return this.http.get(`${this.base}/admin/bills/${bookingId}/invoice`, { responseType: 'blob' });
  }

  getBillForBooking(bookingId: string): Observable<ApiResponse<Bill>> {
    return this.http.get<ApiResponse<Bill>>(`${this.base}/customer/bills/booking/${bookingId}`);
  }
}
