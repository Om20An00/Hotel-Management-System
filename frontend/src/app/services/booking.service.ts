import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse, Booking, Page } from '../models/models';

@Injectable({ providedIn: 'root' })
export class BookingService {
  private base = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  createBooking(data: any): Observable<ApiResponse<Booking>> {
    return this.http.post<ApiResponse<Booking>>(`${this.base}/customer/bookings`, data);
  }

  payBooking(bookingId: string, paymentData: any): Observable<ApiResponse<Booking>> {
    return this.http.post<ApiResponse<Booking>>(`${this.base}/customer/bookings/${bookingId}/pay`, paymentData);
  }

  getUpcoming(): Observable<ApiResponse<Booking[]>> {
    return this.http.get<ApiResponse<Booking[]>>(`${this.base}/customer/bookings/upcoming`);
  }

  getPast(): Observable<ApiResponse<Booking[]>> {
    return this.http.get<ApiResponse<Booking[]>>(`${this.base}/customer/bookings/past`);
  }

  getBooking(bookingId: string): Observable<ApiResponse<Booking>> {
    return this.http.get<ApiResponse<Booking>>(`${this.base}/customer/bookings/${bookingId}`);
  }

  cancelBooking(bookingId: string): Observable<ApiResponse<Booking>> {
    return this.http.put<ApiResponse<Booking>>(`${this.base}/customer/bookings/${bookingId}/cancel`, {});
  }

  modifyBooking(bookingId: string, data: any): Observable<ApiResponse<Booking>> {
    return this.http.put<ApiResponse<Booking>>(`${this.base}/customer/bookings/${bookingId}/modify`, data);
  }

  downloadInvoice(bookingId: string): Observable<Blob> {
    return this.http.get(`${this.base}/customer/bills/${bookingId}/invoice`, { responseType: 'blob' });
  }

  adminGetAll(page = 0, size = 10, query = ''): Observable<ApiResponse<Page<Booking>>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (query) params = params.set('query', query);
    return this.http.get<ApiResponse<Page<Booking>>>(`${this.base}/admin/bookings`, { params });
  }

  adminCreate(data: any): Observable<ApiResponse<Booking>> {
    return this.http.post<ApiResponse<Booking>>(`${this.base}/admin/bookings`, data);
  }

  adminUpdate(bookingId: string, data: any): Observable<ApiResponse<Booking>> {
    return this.http.put<ApiResponse<Booking>>(`${this.base}/admin/bookings/${bookingId}`, data);
  }

  adminCancel(bookingId: string): Observable<ApiResponse<Booking>> {
    return this.http.put<ApiResponse<Booking>>(`${this.base}/admin/bookings/${bookingId}/cancel`, {});
  }

  getDashboardStats(): Observable<ApiResponse<any>> {
    return this.http.get<ApiResponse<any>>(`${this.base}/admin/bookings/stats`);
  }
}
