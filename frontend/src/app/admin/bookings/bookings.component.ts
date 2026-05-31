import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { BookingService } from '../../services/booking.service';
import { UserService } from '../../services/user.service';
import { RoomService } from '../../services/room.service';
import { Booking } from '../../models/models';

@Component({
  selector: 'app-bookings',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './bookings.component.html'
})
export class BookingsComponent implements OnInit {
  bookings: Booking[] = [];
  total = 0; page = 0; size = 10; totalPages = 0;
  query = ''; loading = true;
  error = ''; success = '';
  showAdd = false; showEdit = false;
  newBooking: any = { numberOfAdults: 1, numberOfChildren: 0 };
  editBooking: any = null;
  today = new Date().toISOString().split('T')[0];

  constructor(private bookingService: BookingService) {}

  ngOnInit() { this.load(); }

  load() {
    this.loading = true;
    this.bookingService.adminGetAll(this.page, this.size, this.query).subscribe({
      next: r => { const d = r.data; this.bookings = d.content; this.total = d.totalElements; this.totalPages = d.totalPages; this.loading = false; },
      error: () => this.loading = false
    });
  }

  search() { this.page = 0; this.load(); }

  submitAdd() {
    this.bookingService.adminCreate(this.newBooking).subscribe({
      next: r => { if (r.success) { this.success = 'You have successfully reserved the room. Booking ID: ' + r.data.bookingId; this.showAdd = false; this.newBooking = { numberOfAdults: 1, numberOfChildren: 0 }; this.load(); } else this.error = r.message; },
      error: err => this.error = err.error?.message || 'Create failed.'
    });
  }

  openEdit(b: Booking) { this.editBooking = { ...b, checkInDate: b.checkInDate, checkOutDate: b.checkOutDate, roomId: b.room?.id }; this.showEdit = true; this.showAdd = false; }

  submitEdit() {
    this.bookingService.adminUpdate(this.editBooking.bookingId, this.editBooking).subscribe({
      next: r => { if (r.success) { this.success = 'Booking updated.'; this.showEdit = false; this.load(); } else this.error = r.message; },
      error: err => this.error = err.error?.message || 'Update failed.'
    });
  }

  cancel(bookingId: string) {
    if (!confirm('Cancel this booking?')) return;
    this.bookingService.adminCancel(bookingId).subscribe({
      next: r => { if (r.success) { this.success = 'Booking cancelled.'; this.load(); } else this.error = r.message; },
      error: err => this.error = err.error?.message || 'Cancel failed.'
    });
  }
}
