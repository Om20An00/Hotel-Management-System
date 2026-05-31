import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './login.component.html'
})
export class LoginComponent {
  username = '';
  password = '';
  error = '';
  loading = false;
  showPassword = false;
  submitted = false;

  constructor(private auth: AuthService, private router: Router) {}

  login() {
    this.submitted = true;

    if (!this.username || !this.password) {
      this.error = 'Please enter username and password.';
      return;
    }

    this.loading = true;
    this.error = '';

    this.auth.login({
      username: this.username,
      password: this.password
    }).subscribe({
      next: (res: any) => {
        this.loading = false;

        if (res?.success) {
          const role = res.data?.role;
          if (role === 'ADMIN') {
            this.router.navigate(['/admin/dashboard']);
          } else if (role === 'STAFF') {
            this.router.navigate(['/staff/dashboard']);
          } else {
            this.router.navigate(['/customer/home']);
          }
        } else {
          this.error = res?.message || 'Login failed.';
        }
      },
      error: err => {
        this.loading = false;
        this.error = err?.error?.message || 'Login failed.';
      }
    });
  }
}