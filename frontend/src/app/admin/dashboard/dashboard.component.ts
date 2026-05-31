import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router } from '@angular/router';
import { BookingService } from '../../services/booking.service';
import { RoomService } from '../../services/room.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.component.html'
})
export class DashboardComponent implements OnInit {
  adminName = '';
  stats: any = {};
  availableRooms = 0;
  loading = true;

  constructor(private bookingService: BookingService, private roomService: RoomService, private auth: AuthService, private router: Router) {}

  ngOnInit() {
    this.adminName = this.auth.getCustomerName() || 'Admin';
    this.bookingService.getDashboardStats().subscribe({ next: r => { this.stats = r.data; this.loading = false; }, error: () => this.loading = false });
    this.roomService.adminGetAll(0, 1000).subscribe({ next: r => { this.availableRooms = (r.data?.content || []).filter((rm: any) => rm.status === 'AVAILABLE').length; } });
  }

  logout() { this.auth.logout(); this.router.navigate(['/auth/login']); }
}
