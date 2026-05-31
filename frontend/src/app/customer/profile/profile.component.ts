import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { UserService } from '../../services/user.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './profile.component.html'
})
export class ProfileComponent implements OnInit {
  profile: any = {};
  form: any = {};
  errors: any = {};
  loading = false; saving = false;
  error = ''; success = '';
  editing = false;

  constructor(private userService: UserService) {}

  ngOnInit() {
    this.loading = true;
    this.userService.getProfile().subscribe({ next: r => { this.profile = r.data; this.form = { ...r.data }; this.loading = false; }, error: () => this.loading = false });
  }

  validate(): boolean {
    this.errors = {};
    if (!this.form.customerName || this.form.customerName.length < 2) this.errors.customerName = 'Name must be at least 2 characters.';
    if (!this.form.email || !/^[^@]+@[^@]+\.[^@]+$/.test(this.form.email)) this.errors.email = 'Please enter a valid email address.';
    if (!this.form.mobileNumber || !/^\d{8,10}$/.test(this.form.mobileNumber)) this.errors.mobileNumber = 'Please enter a valid phone number with country code.';
    return Object.keys(this.errors).length === 0;
  }

  save() {
    if (!this.validate()) return;
    this.saving = true; this.error = '';
    this.userService.updateProfile(this.form).subscribe({
      next: r => { this.saving = false; if (r.success) { this.profile = r.data; this.success = 'Your profile has been updated successfully.'; this.editing = false; } else this.error = r.message; },
      error: err => { this.saving = false; this.error = err.error?.message || 'Update failed.'; }
    });
  }
}
