import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { BookingService } from '../../services/booking.service';
import { Booking } from '../../models/models';

@Component({
  selector: 'app-booking-detail',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
  <nav class="navbar navbar-dark bg-primary">
    <div class="container"><a class="navbar-brand" routerLink="/customer/booking-history"><i class="bi bi-arrow-left me-2"></i>Booking Details</a></div>
  </nav>
  <div class="container py-4" style="max-width:700px">
    <div *ngIf="loading" class="text-center py-5"><div class="spinner-border text-primary"></div></div>
    <div *ngIf="booking" class="card shadow">
      <div class="card-header bg-primary text-white"><strong>Booking ID: {{booking.bookingId}}</strong></div>
      <div class="card-body">
        <div class="row g-3">
          <div class="col-6"><strong>Room:</strong> {{booking.room?.roomNumber}}</div>
          <div class="col-6"><strong>Type:</strong> {{booking.room?.roomType}}</div>
          <div class="col-6"><strong>Check-in:</strong> {{booking.checkInDate}}</div>
          <div class="col-6"><strong>Check-out:</strong> {{booking.checkOutDate}}</div>
          <div class="col-6"><strong>Guests:</strong> {{booking.numberOfAdults}} Adults, {{booking.numberOfChildren}} Children</div>
          <div class="col-6"><strong>Status:</strong> <span class="badge bg-success">{{booking.status}}</span></div>
          <div class="col-6"><strong>Payment:</strong> <span class="badge" [class.bg-success]="booking.paymentStatus==='PAID'" [class.bg-warning]="booking.paymentStatus!='PAID'">{{booking.paymentStatus}}</span></div>
          <div class="col-6"><strong>Method:</strong> {{booking.paymentMethod || 'N/A'}}</div>
          <div class="col-6"><strong>Base Price:</strong> ₹{{booking.basePrice | number:'1.2-2'}}</div>
          <div class="col-6"><strong>Tax (18%):</strong> ₹{{booking.taxAmount | number:'1.2-2'}}</div>
          <div class="col-12"><strong class="fs-5">Total: ₹{{booking.totalAmount | number:'1.2-2'}}</strong></div>
          <div class="col-12" *ngIf="booking.specialRequests"><strong>Special Requests:</strong> {{booking.specialRequests}}</div>
          <div class="col-12" *ngIf="booking.transactionId"><strong>Transaction ID:</strong> {{booking.transactionId}}</div>
        </div>
      </div>
      <div class="card-footer d-flex gap-2">
        <button class="btn btn-success" (click)="downloadInvoice()" *ngIf="booking.paymentStatus==='PAID'"><i class="bi bi-download me-2"></i>Download Invoice</button>
        <a routerLink="/customer/booking-history" class="btn btn-outline-secondary">Back</a>
      </div>
    </div>
  </div>`
})
export class BookingDetailComponent implements OnInit {
  booking: Booking | null = null;
  loading = true;

  constructor(private route: ActivatedRoute, private bookingService: BookingService) {}

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id')!;
    this.bookingService.getBooking(id).subscribe({ next: r => { this.booking = r.data; this.loading = false; }, error: () => this.loading = false });
  }

  downloadInvoice() {
    this.bookingService.downloadInvoice(this.booking!.bookingId!).subscribe(blob => {
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url; a.download = 'invoice-' + this.booking!.bookingId + '.pdf'; a.click();
      window.URL.revokeObjectURL(url);
    });
  }
}
