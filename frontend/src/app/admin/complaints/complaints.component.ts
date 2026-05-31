import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ComplaintService } from '../../services/complaint.service';
import { UserService } from '../../services/user.service';
import { Complaint } from '../../models/models';

@Component({
  selector: 'app-admin-complaints',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './complaints.component.html'
})
export class AdminComplaintsComponent implements OnInit {
  complaints: Complaint[] = [];
  total = 0; page = 0; size = 10; totalPages = 0;
  query = ''; loading = true;
  error = ''; success = '';
  staffList: any[] = [];
  selected: Complaint | null = null;
  statusUpdate = ''; responseText = ''; actionText = ''; assignStaffId = 0;

  constructor(private complaintService: ComplaintService, private userService: UserService) {}

  ngOnInit() { this.load(); this.loadStaff(); }

  load() {
    this.loading = true;
    this.complaintService.adminGetAll(this.page, this.size, this.query).subscribe({
      next: r => { const d = r.data; this.complaints = d.content; this.total = d.totalElements; this.totalPages = d.totalPages; this.loading = false; },
      error: () => this.loading = false
    });
  }

  loadStaff() {
    this.userService.adminGetStaff().subscribe({ next: r => this.staffList = r.data?.content || [], error: () => {} });
  }

  search() { this.page = 0; this.load(); }

  select(c: Complaint) { this.selected = c; this.statusUpdate = c.status || ''; this.responseText = c.response || ''; this.actionText = ''; this.assignStaffId = c.assignedStaff?.id || 0; }

  updateStatus() {
    if (!this.selected) return;
    this.complaintService.adminUpdateStatus(this.selected.complaintId!, this.statusUpdate, this.responseText, this.actionText).subscribe({
      next: r => { this.success = 'Status updated.'; this.selected = null; this.load(); },
      error: err => this.error = err.error?.message || 'Update failed.'
    });
  }

  assign() {
    if (!this.selected || !this.assignStaffId) return;
    this.complaintService.adminAssign(this.selected.complaintId!, this.assignStaffId).subscribe({
      next: r => { this.success = 'Complaint assigned to staff.'; this.selected = null; this.load(); },
      error: err => this.error = err.error?.message || 'Assign failed.'
    });
  }

  statusClass(s: string) {
    return { 'bg-warning text-dark': s==='OPEN', 'bg-info text-dark': s==='IN_PROGRESS', 'bg-success': s==='RESOLVED', 'bg-secondary': s==='CLOSED' };
  }
}
