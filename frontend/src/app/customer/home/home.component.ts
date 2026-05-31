import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './home.component.html'
})
export class HomeComponent implements OnInit {
  customerName = '';
  showProfileMenu = false;

  constructor(private auth: AuthService, private router: Router) {}

  ngOnInit() { this.customerName = this.auth.getCustomerName() || ''; }

  logout() { this.auth.logout(); this.router.navigate(['/auth/login']); }
}
