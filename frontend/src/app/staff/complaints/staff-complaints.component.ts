import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ComplaintService } from '../../services/complaint.service';
import { Complaint } from '../../models/models';

@Component({
  selector: 'app-staff-complaints',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './staff-complaints.component.html'
})
export class StaffComplaintsComponent implements OnInit {
  complaints: Complaint[] = [];
  loading = true; error = ''; success = '';
  selected: Complaint | null = null;
  statusUpdate = ''; responseText = ''; actionLog = '';

  constructor(private svc: ComplaintService) {}

  ngOnInit() { this.load(); }

  load() { this.svc.staffGetMine().subscribe({ next: r => { this.complaints = r.data || []; this.loading = false; }, error: () => this.loading = false }); }

  select(c: Complaint) { this.selected = c; this.statusUpdate = c.status || ''; this.responseText = c.response || ''; this.actionLog = ''; }

  update() {
    if (!this.selected) return;
    this.svc.staffUpdateStatus(this.selected.complaintId!, this.statusUpdate, this.responseText, this.actionLog).subscribe({
      next: () => { this.success = 'Status updated.'; this.selected = null; this.load(); },
      error: err => this.error = err.error?.message || 'Update failed.'
    });
  }

  statusClass(s: string) {
    return { 'bg-warning text-dark': s==='OPEN', 'bg-info text-dark': s==='IN_PROGRESS', 'bg-success': s==='RESOLVED', 'bg-secondary': s==='CLOSED' };
  }
}
