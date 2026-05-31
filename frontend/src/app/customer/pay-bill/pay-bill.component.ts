import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute, RouterLink } from '@angular/router';
import { BookingService } from '../../services/booking.service';

@Component({
  selector: 'app-pay-bill',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './pay-bill.component.html'
})
export class PayBillComponent implements OnInit {
  booking: any;
  bookingId = '';
  card = { cardholderName: '', cardNumber: '', expiryDate: '', cvv: '', billingAddress: '' };
  errors: any = {};
  loading = false;
  error = '';
  paid = false;
  paidBooking: any;

  constructor(private route: ActivatedRoute, private router: Router, private bookingService: BookingService) {}

  ngOnInit() {
    this.bookingId = this.route.snapshot.paramMap.get('bookingId') || '';
    const state = history.state;
    this.booking = state['booking'];
  }

  validate(): boolean {
    this.errors = {};
    if (!this.card.cardholderName || this.card.cardholderName.length < 3 || !/^[a-zA-Z ]+$/.test(this.card.cardholderName))
      this.errors.cardholderName = 'Enter valid cardholder name.';
    if (!this.card.cardNumber || !/^\d{16}$/.test(this.card.cardNumber.replace(/\s/g,'')))
      this.errors.cardNumber = 'Invalid card number. Must be 16 digits.';
    if (!this.card.expiryDate || !/^(0[1-9]|1[0-2])\/\d{2}$/.test(this.card.expiryDate))
      this.errors.expiryDate = 'Card expiry date must be in the future. Format MM/YY.';
    if (!this.card.cvv || !/^\d{3,4}$/.test(this.card.cvv))
      this.errors.cvv = 'Invalid CVV. Please check again.';
    return Object.keys(this.errors).length === 0;
  }

  pay() {
    if (!this.validate()) return;
    this.loading = true; this.error = '';
    this.bookingService.payBooking(this.bookingId, this.card).subscribe({
      next: res => {
        this.loading = false;
        if (res.success) { this.paid = true; this.paidBooking = res.data; }
        else this.error = res.message;
      },
      error: err => { this.loading = false; this.error = 'Transaction failed. Please check your details and try again.'; }
    });
  }

  downloadInvoice() {
    this.bookingService.downloadInvoice(this.bookingId).subscribe(blob => {
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url; a.download = 'invoice-' + this.bookingId + '.pdf'; a.click();
      window.URL.revokeObjectURL(url);
    });
  }
}
