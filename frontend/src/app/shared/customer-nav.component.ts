import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-customer-nav',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  template: `
    <nav class="nav">
      <a routerLink="/customer/home" class="nav-brand">🏨 Grand Hotel</a>
      <div class="nav-links">
        <a routerLink="/customer/home" routerLinkActive="active" class="nav-link">Home</a>
        <a routerLink="/customer/search-rooms" routerLinkActive="active" class="nav-link">Search Rooms</a>
        <a routerLink="/customer/booking-history" routerLinkActive="active" class="nav-link">My Bookings</a>
        <a routerLink="/customer/complaints" routerLinkActive="active" class="nav-link">Complaints</a>
        <a routerLink="/customer/profile" routerLinkActive="active" class="nav-link">Profile</a>
        <button class="nav-link btn-danger" style="background:#dc2626;color:#fff;padding:6px 14px;border-radius:6px;" (click)="logout()">Logout</button>
      </div>
      <span class="nav-user">Welcome, {{ name }}!</span>
    </nav>
  `
})
export class CustomerNavComponent {
  name = localStorage.getItem('customerName') || 'Customer';
  constructor(private auth: AuthService, private router: Router) {}
  logout() { this.auth.logout(); this.router.navigate(['/auth/login']); }
}
