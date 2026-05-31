import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { UserService } from '../../services/user.service';
import { User } from '../../models/models';

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './users.component.html'
})
export class UsersComponent implements OnInit {
  users: User[] = [];
  total = 0; page = 0; size = 10; totalPages = 0;
  query = ''; loading = true;
  error = ''; success = '';
  showAdd = false; showEdit = false;
  newUser: any = { role: 'CUSTOMER' };
  editUser: any = null;

  constructor(private userService: UserService) {}

  ngOnInit() { this.load(); }

  load() {
    this.loading = true;
    this.userService.adminGetAll(this.page, this.size, this.query).subscribe({
      next: r => { const d = r.data; this.users = d.content; this.total = d.totalElements; this.totalPages = d.totalPages; this.loading = false; },
      error: () => this.loading = false
    });
  }

  search() { this.page = 0; this.load(); }

  submitAdd() {
    this.userService.adminCreate(this.newUser).subscribe({
      next: r => { if (r.success) { this.success = 'User created. Temp password: ' + (r.data.address || ''); this.showAdd = false; this.newUser = { role: 'CUSTOMER' }; this.load(); } else this.error = r.message; },
      error: err => this.error = err.error?.message || 'Create failed.'
    });
  }

  openEdit(u: User) { this.editUser = { ...u }; this.showEdit = true; this.showAdd = false; }

  submitEdit() {
    this.userService.adminUpdate(this.editUser.id!, this.editUser).subscribe({
      next: r => { if (r.success) { this.success = 'User updated.'; this.showEdit = false; this.load(); } else this.error = r.message; },
      error: err => this.error = err.error?.message || 'Update failed.'
    });
  }

  toggleStatus(u: User) {
    const msg = u.status === 'ACTIVE' ? 'Are you sure you want to deactivate the customer?' : 'Reactivate this user?';
    if (!confirm(msg)) return;
    this.userService.adminToggleStatus(u.id!).subscribe({
      next: r => { const act = r.data.status === 'ACTIVE' ? 'activated' : 'deactivated'; this.success = `User ${act} successfully.`; this.load(); },
      error: err => this.error = err.error?.message || 'Failed.'
    });
  }

  resetPassword(u: User) {
    if (!confirm('Reset password for ' + u.username + '?')) return;
    this.userService.adminResetPassword(u.id!).subscribe({
      next: r => this.success = 'Password reset. Temp: ' + (r.data?.address || 'check logs'),
      error: err => this.error = err.error?.message || 'Reset failed.'
    });
  }
}
