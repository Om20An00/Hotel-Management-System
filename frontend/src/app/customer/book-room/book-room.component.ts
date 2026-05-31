import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute, RouterLink } from '@angular/router';
import { BookingService } from '../../services/booking.service';

@Component({
  selector: 'app-book-room',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './book-room.component.html'
})
export class BookRoomComponent implements OnInit {
  room: any;
  search: any;
  paymentMethod = '';
  specialRequests = '';
  nights = 0;
  total = 0;
  base = 0;
  tax = 0;
  loading = false;
  error = '';
  errors: any = {};

  constructor(private route: ActivatedRoute, private router: Router, private bookingService: BookingService) {}

  ngOnInit() {
    const nav = this.router.getCurrentNavigation() || history.state;
    const state = (nav as any)?.extras?.state || history.state;
    this.room = state['room'];
    this.search = state['search'];
    if (!this.room) { this.router.navigate(['/customer/search-rooms']); return; }
    this.calcTotal();
  }

  calcTotal() {
    if (this.search?.checkIn && this.search?.checkOut) {
      const d1 = new Date(this.search.checkIn), d2 = new Date(this.search.checkOut);
      this.nights = Math.ceil((d2.getTime() - d1.getTime()) / 86400000);
      this.base = this.room.pricePerNight * this.nights;
      this.tax = this.base * 0.18;
      this.total = this.base + this.tax;
    }
  }

  proceed() {
    this.errors = {};
    if (!this.paymentMethod) { this.errors.paymentMethod = 'Please select a payment method.'; return; }
    this.loading = true;
    this.bookingService.createBooking({
      roomId: this.room.id,
      checkInDate: this.search.checkIn,
      checkOutDate: this.search.checkOut,
      numberOfAdults: this.search.adults,
      numberOfChildren: this.search.children,
      specialRequests: this.specialRequests,
      paymentMethod: this.paymentMethod
    }).subscribe({
      next: res => {
        this.loading = false;
        if (res.success) this.router.navigate(['/customer/pay-bill', res.data.bookingId], { state: { booking: res.data } });
        else this.error = res.message;
      },
      error: err => { this.loading = false; this.error = err.error?.message || 'Booking failed.'; }
    });
  }
}
