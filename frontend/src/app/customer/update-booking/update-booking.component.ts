import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { BookingService } from '../../services/booking.service';

@Component({
  selector: 'app-update-booking',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
  <nav class="navbar navbar-dark bg-primary">
    <div class="container"><a class="navbar-brand" routerLink="/customer/booking-history"><i class="bi bi-arrow-left me-2"></i>Modify Booking</a></div>
  </nav>
  <div class="container py-4" style="max-width:600px">
    <h3 class="mb-4">Modify Booking</h3>
    <div *ngIf="error" class="alert alert-danger">{{error}}</div>
    <div *ngIf="success" class="alert alert-success">Your booking has been successfully modified.</div>
    <div class="card shadow-sm">
      <div class="card-body">
        <div class="mb-3">
          <label class="form-label">Check-in Date *</label>
          <input [(ngModel)]="form.checkInDate" type="date" class="form-control" [min]="today">
        </div>
        <div class="mb-3">
          <label class="form-label">Check-out Date *</label>
          <input [(ngModel)]="form.checkOutDate" type="date" class="form-control">
        </div>
        <div class="mb-3">
          <label class="form-label">Number of Adults *</label>
          <input [(ngModel)]="form.numberOfAdults" type="number" class="form-control" min="1" max="10">
        </div>
        <div class="mb-3">
          <label class="form-label">Number of Children</label>
          <input [(ngModel)]="form.numberOfChildren" type="number" class="form-control" min="0" max="5">
        </div>
      </div>
    </div>
    <button class="btn btn-primary w-100 mt-3" (click)="modify()" [disabled]="loading">
      <span *ngIf="loading" class="spinner-border spinner-border-sm me-2"></span>Confirm Modification
    </button>
  </div>`
})
export class UpdateBookingComponent implements OnInit {
  bookingId = '';
  form: any = { numberOfAdults: 1, numberOfChildren: 0 };
  today = new Date().toISOString().split('T')[0];
  loading = false; error = ''; success = false;

  constructor(private route: ActivatedRoute, private router: Router, private bookingService: BookingService) {}

  ngOnInit() {
    this.bookingId = this.route.snapshot.paramMap.get('id')!;
    this.bookingService.getBooking(this.bookingId).subscribe(r => {
      if (r.data) {
        this.form.checkInDate = r.data.checkInDate;
        this.form.checkOutDate = r.data.checkOutDate;
        this.form.numberOfAdults = r.data.numberOfAdults;
        this.form.numberOfChildren = r.data.numberOfChildren;
        this.form.roomId = r.data.room?.id;
      }
    });
  }

  modify() {
    if (!this.form.checkInDate || !this.form.checkOutDate) { this.error = 'Please fill all required fields.'; return; }
    this.loading = true; this.error = '';
    this.bookingService.modifyBooking(this.bookingId, this.form).subscribe({
      next: r => { this.loading = false; if (r.success) { this.success = true; setTimeout(() => this.router.navigate(['/customer/booking-history']), 2000); } else this.error = r.message; },
      error: err => { this.loading = false; this.error = err.error?.message || 'Modification failed.'; }
    });
  }
}
