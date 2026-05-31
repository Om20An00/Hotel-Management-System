import {Routes} from '@angular/router';
import { authGuard } from './guards/auth.guard';
import { roleGuard } from './guards/role.guard';

export const routes: Routes = [
  { path: '', redirectTo: '/auth/login', pathMatch: 'full' },

  {
    path: 'auth',
    children: [
      {
        path: 'login',
        loadComponent: () =>
          import('./auth/login/login.component').then(m => m.LoginComponent)
      },
      {
        path: 'register',
        loadComponent: () =>
          import('./auth/register/register.component').then(m => m.RegisterComponent)
      },
      {
        path: 'register-success',
        loadComponent: () =>
          import('./auth/register-success/register-success.component')
            .then(m => m.RegisterSuccessComponent)
      }
    ]
  },

  {
    path: 'customer',
    canActivate: [authGuard, roleGuard],
    data: { roles: ['CUSTOMER'] },
    children: [
      { path: '', redirectTo: 'home', pathMatch: 'full' },
      { path: 'home', loadComponent: () => import('./customer/home/home.component').then(m => m.HomeComponent) },
      { path: 'search-rooms', loadComponent: () => import('./customer/search-rooms/search-rooms.component').then(m => m.SearchRoomsComponent) },
      { path: 'book-room/:id', loadComponent: () => import('./customer/book-room/book-room.component').then(m => m.BookRoomComponent) },
      { path: 'pay-bill/:bookingId', loadComponent: () => import('./customer/pay-bill/pay-bill.component').then(m => m.PayBillComponent) },
      { path: 'booking-history', loadComponent: () => import('./customer/booking-history/booking-history.component').then(m => m.BookingHistoryComponent) },
      { path: 'booking-detail/:id', loadComponent: () => import('./customer/booking-detail/booking-detail.component').then(m => m.BookingDetailComponent) },
      { path: 'update-booking/:id', loadComponent: () => import('./customer/update-booking/update-booking.component').then(m => m.UpdateBookingComponent) },
      { path: 'complaints', loadComponent: () => import('./customer/complaints/complaints.component').then(m => m.ComplaintsComponent) },
      { path: 'profile', loadComponent: () => import('./customer/profile/profile.component').then(m => m.ProfileComponent) }
    ]
  },

  {
    path: 'admin',
    canActivate: [authGuard, roleGuard],
    data: { roles: ['ADMIN'] },
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', loadComponent: () => import('./admin/dashboard/dashboard.component').then(m => m.DashboardComponent) },
      { path: 'rooms', loadComponent: () => import('./admin/rooms/rooms.component').then(m => m.RoomsComponent) },
      { path: 'bookings', loadComponent: () => import('./admin/bookings/bookings.component').then(m => m.BookingsComponent) },
      { path: 'users', loadComponent: () => import('./admin/users/users.component').then(m => m.UsersComponent) },
      { path: 'bills', loadComponent: () => import('./admin/bills/bills.component').then(m => m.BillsComponent) },
      { path: 'complaints', loadComponent: () => import('./admin/complaints/complaints.component').then(m => m.AdminComplaintsComponent) }
    ]
  },

  {
    path: 'staff',
    canActivate: [authGuard, roleGuard],
    data: { roles: ['STAFF'] },
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', loadComponent: () => import('./staff/dashboard/staff-dashboard.component').then(m => m.StaffDashboardComponent) },
      { path: 'complaints', loadComponent: () => import('./staff/complaints/staff-complaints.component').then(m => m.StaffComplaintsComponent) }
    ]
  },

  { path: '**', redirectTo: '/auth/login' }
];
