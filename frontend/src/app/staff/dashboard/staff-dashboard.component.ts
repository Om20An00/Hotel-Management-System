import { Component } from '@angular/core';
import { RouterLink, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-staff-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
  <nav class="navbar navbar-dark bg-dark">
    <div class="container-fluid">
      <span class="navbar-brand"><i class="bi bi-building me-2"></i>Grand Hotel – Staff</span>
      <div class="d-flex gap-3 align-items-center">
        <span class="text-white">{{name}}</span>
        <button class="btn btn-outline-light btn-sm" (click)="logout()"><i class="bi bi-box-arrow-right me-1"></i>Logout</button>
      </div>
    </div>
  </nav>
  <div class="container py-5 text-center">
    <h2 class="mb-4">Welcome, {{name}}!</h2>
    <div class="row justify-content-center g-4">
      <div class="col-md-4">
        <div class="card shadow p-4 h-100">
          <i class="bi bi-chat-square-dots fs-1 text-primary mb-3"></i>
          <h5>My Complaints</h5>
          <p class="text-muted">View and resolve complaints assigned to you</p>
          <a routerLink="/staff/complaints" class="btn btn-primary mt-auto">View Complaints</a>
        </div>
      </div>
    </div>
  </div>`
})
export class StaffDashboardComponent {
  name = '';
  constructor(private auth: AuthService, private router: Router) { this.name = this.auth.getCustomerName() || ''; }
  logout() { this.auth.logout(); this.router.navigate(['/auth/login']); }
}
