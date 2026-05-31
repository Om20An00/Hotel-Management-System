import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ComplaintService } from '../../services/complaint.service';
import { Complaint } from '../../models/models';

@Component({
  selector: 'app-complaints',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './complaints.component.html'
})
export class ComplaintsComponent implements OnInit {
  complaints: Complaint[] = [];
  showForm = false;
  form: any = {};
  errors: any = {};
  loading = false; listLoading = true;
  error = ''; success = '';
  selected: Complaint | null = null;

  constructor(private svc: ComplaintService) {}

  ngOnInit() { this.load(); }

  load() {
    this.svc.getMine().subscribe({ next: r => { this.complaints = r.data || []; this.listLoading = false; }, error: () => this.listLoading = false });
  }

  validate(): boolean {
    this.errors = {};
    if (!this.form.category) this.errors.category = 'Please select a category.';
    if (!this.form.title || this.form.title.length < 10) this.errors.title = 'Title must be at least 10 characters.';
    if (!this.form.description || this.form.description.length < 20) this.errors.description = 'Description must be at least 20 characters.';
    if (!this.form.contactPreference) this.errors.contactPreference = 'Please select contact preference.';
    return Object.keys(this.errors).length === 0;
  }

  submit() {
    if (!this.validate()) return;
    this.loading = true; this.error = '';
    this.svc.submit(this.form).subscribe({
      next: r => {
        this.loading = false;
        if (r.success) {
          this.success = 'Your complaint has been successfully submitted. Complaint ID: ' + r.data.complaintId;
          this.showForm = false; this.form = {}; this.load();
        } else this.error = r.message;
      },
      error: err => { this.loading = false; this.error = err.error?.message || 'Submission failed.'; }
    });
  }

  confirm(id: string) { this.svc.confirmResolution(id).subscribe(() => this.load()); }
  reopen(id: string) { this.svc.reopen(id).subscribe(() => this.load()); }
  reset() { this.form = {}; this.errors = {}; }

  statusClass(s: string) {
    return { 'bg-warning': s==='OPEN', 'bg-info': s==='IN_PROGRESS', 'bg-success': s==='RESOLVED', 'bg-secondary': s==='CLOSED' };
  }
}
