import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { BillService } from '../../services/bill.service';
import { Bill } from '../../models/models';

@Component({
  selector: 'app-bills',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './bills.component.html'
})
export class BillsComponent implements OnInit {
  bills: Bill[] = [];
  total = 0; page = 0; size = 10; totalPages = 0;
  query = ''; loading = true;
  error = ''; success = '';
  showAdd = false; showEdit = false;
  newBill: any = { roomCharges: 0, serviceCharges: 0, additionalFees: 0, taxAmount: 0, discount: 0 };
  editBill: any = null;

  constructor(private billService: BillService) {}

  ngOnInit() { this.load(); }

  load() {
    this.loading = true;
    this.billService.adminGetAll(this.page, this.size, this.query).subscribe({
      next: r => { const d = r.data; this.bills = d.content; this.total = d.totalElements; this.totalPages = d.totalPages; this.loading = false; },
      error: () => this.loading = false
    });
  }

  search() { this.page = 0; this.load(); }

  submitAdd() {
    this.billService.adminCreate(this.newBill).subscribe({
      next: r => { if (r.success) { this.success = 'Bill created.'; this.showAdd = false; this.load(); } else this.error = r.message; },
      error: err => this.error = err.error?.message || 'Create failed.'
    });
  }

  openEdit(b: Bill) { this.editBill = { ...b }; this.showEdit = true; this.showAdd = false; }

  submitEdit() {
    this.billService.adminUpdate(this.editBill.billId, this.editBill).subscribe({
      next: r => { if (r.success) { this.success = 'Bill updated.'; this.showEdit = false; this.load(); } else this.error = r.message; },
      error: err => this.error = err.error?.message || 'Update failed.'
    });
  }
}
