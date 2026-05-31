import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './register.component.html'
})
export class RegisterComponent {
  form: any = {
    customerName: '',
    email: '',
    countryCode: '+91',
    mobileNumber: '',
    address: '',
    username: '',
    password: '',
    confirmPassword: ''
  };

  errors: any = {};
  serverError = '';
  loading = false;

  constructor(private auth: AuthService, private router: Router) {}

  validate(): boolean {
    this.errors = {};

    if (!this.form.customerName || this.form.customerName.length < 3 || !/^[a-zA-Z ]+$/.test(this.form.customerName)) {
      this.errors.customerName = 'Name must be at least 3 characters and contain only letters.';
    }

    if (!this.form.email || !/^[^@]+@[^@]+\.[^@]+$/.test(this.form.email)) {
      this.errors.email = 'Enter a valid email address.';
    }

    if (!this.form.mobileNumber || !/^\d{8,10}$/.test(this.form.mobileNumber)) {
      this.errors.mobileNumber = 'Enter a valid mobile number (8-10 digits).';
    }

    if (!this.form.address || this.form.address.length < 10) {
      this.errors.address = 'Address must be at least 10 characters long.';
    }

    if (!this.form.username || this.form.username.length < 5 || /\s/.test(this.form.username)) {
      this.errors.username = 'Username must be at least 5 characters and must not contain spaces.';
    }

    if (!this.form.password ||
        !/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^a-zA-Z0-9]).{8,}$/.test(this.form.password)) {
      this.errors.password =
        'Password must contain uppercase, lowercase, number, and special character.';
    }

    if (this.form.password !== this.form.confirmPassword) {
      this.errors.confirmPassword = 'Passwords do not match.';
    }

    return Object.keys(this.errors).length === 0;
  }

  register() {
    if (!this.validate()) return;

    this.loading = true;
    this.serverError = '';

    this.auth.register(this.form).subscribe({
      next: (res: any) => {
        this.loading = false;
        if (res?.success) {
          this.router.navigate(['/auth/register-success']);
        } else {
          this.serverError = res?.message || 'Registration failed.';
        }
      },
      error: err => {
        this.loading = false;
        this.serverError = err?.error?.message || 'Registration failed.';
      }
    });
  }

  reset() {
    this.form = {
      customerName: '',
      email: '',
      countryCode: '+91',
      mobileNumber: '',
      address: '',
      username: '',
      password: '',
      confirmPassword: ''
    };
    this.errors = {};
    this.serverError = '';
  }
}