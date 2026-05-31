import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse, Room, Page } from '../models/models';

@Injectable({ providedIn: 'root' })
export class RoomService {
  private base = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  getAvailableRooms(checkIn: string, checkOut: string, roomType?: string): Observable<ApiResponse<Room[]>> {
    let params = new HttpParams().set('checkIn', checkIn).set('checkOut', checkOut);
    if (roomType) params = params.set('roomType', roomType);
    return this.http.get<ApiResponse<Room[]>>(`${this.base}/rooms/available`, { params });
  }

  getRoomById(id: number): Observable<ApiResponse<Room>> {
    return this.http.get<ApiResponse<Room>>(`${this.base}/rooms/${id}`);
  }

  adminGetAll(page = 0, size = 10, sort = 'roomNumber', direction = 'asc', query = ''): Observable<ApiResponse<Page<Room>>> {
    let params = new HttpParams().set('page', page).set('size', size).set('sort', sort).set('direction', direction);
    if (query) params = params.set('query', query);
    return this.http.get<ApiResponse<Page<Room>>>(`${this.base}/admin/rooms`, { params });
  }

  adminAddRoom(room: any): Observable<ApiResponse<Room>> {
    return this.http.post<ApiResponse<Room>>(`${this.base}/admin/rooms`, room);
  }

  adminUpdateRoom(id: number, room: any): Observable<ApiResponse<Room>> {
    return this.http.put<ApiResponse<Room>>(`${this.base}/admin/rooms/${id}`, room);
  }

  adminBulkImport(file: File): Observable<ApiResponse<any>> {
    const form = new FormData();
    form.append('file', file);
    return this.http.post<ApiResponse<any>>(`${this.base}/admin/rooms/bulk-import`, form);
  }
}
