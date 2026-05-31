import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { BookingService } from '../../services/booking.service';
import { Booking } from '../../models/models';

@Component({
  selector: 'app-booking-history',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './booking-history.component.html'
})
export class BookingHistoryComponent implements OnInit {
  upcoming: Booking[] = [];
  past: Booking[] = [];
  loading = true;
  error = '';

  constructor(private bookingService: BookingService) {}

  ngOnInit() { this.load(); }

  load() {
    this.bookingService.getUpcoming().subscribe({ next: r => { this.upcoming = r.data || []; this.loading = false; }, error: () => this.loading = false });
    this.bookingService.getPast().subscribe({ next: r => { this.past = r.data || []; }, error: () => {} });
  }

  cancel(bookingId: string) {
    if (!confirm('Are you sure you want to cancel this booking?')) return;
    this.bookingService.cancelBooking(bookingId).subscribe({
      next: () => this.load(),
      error: err => alert(err.error?.message || 'Cancel failed.')
    });
  }

  downloadInvoice(bookingId: string) {
    this.bookingService.downloadInvoice(bookingId).subscribe(blob => {
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url; a.download = 'invoice-' + bookingId + '.pdf'; a.click();
      window.URL.revokeObjectURL(url);
    }, err => alert('Unable to generate invoice. Please try again later.'));
  }
}
