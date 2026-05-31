import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-register-success',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="auth-wrapper">
      <div class="auth-card text-center">
        <i class="bi bi-check-circle-fill text-success fs-1 mb-3"></i>
        <h3>Registration Successful!</h3>
        <p class="text-muted mb-3">Your account has been created.</p>

        <div *ngIf="data" class="alert alert-info text-start">
          <strong>User ID:</strong> {{ data.userId }}<br>
          <strong>Name:</strong> {{ data.customerName }}<br>
          <strong>Email:</strong> {{ data.email }}
        </div>

        <a routerLink="/auth/login" class="btn btn-primary w-100">
          Login Now
        </a>
      </div>
    </div>
  `
})
export class RegisterSuccessComponent {
  data: any = history.state?.data;
}