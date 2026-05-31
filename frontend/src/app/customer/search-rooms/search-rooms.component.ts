import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { RoomService } from '../../services/room.service';
import { Room } from '../../models/models';

@Component({
  selector: 'app-search-rooms',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './search-rooms.component.html'
})
export class SearchRoomsComponent {
  search = { checkIn: '', checkOut: '', adults: 1, children: 0, roomType: '' };
  errors: any = {};
  rooms: Room[] = [];
  filteredRooms: Room[] = [];
  searched = false;
  loading = false;
  serverError = '';
  sortBy = '';
  filterMinPrice = 0;
  filterMaxPrice = 99999;
  today = new Date().toISOString().split('T')[0];

  constructor(private roomService: RoomService, private router: Router) {}

  validate(): boolean {
    this.errors = {};
    if (!this.search.checkIn) this.errors.checkIn = 'Check-in date is required.';
    else if (this.search.checkIn < this.today) this.errors.checkIn = 'Check-in date cannot be in the past.';
    if (!this.search.checkOut) this.errors.checkOut = 'Check-out date is required.';
    else if (this.search.checkOut <= this.search.checkIn) this.errors.checkOut = 'Check-out date must be after check-in date.';
    if (this.search.adults < 1) this.errors.adults = 'At least one adult must be selected.';
    if (this.search.children < 0) this.errors.children = 'Number of children cannot be negative.';
    if (!this.search.roomType) this.errors.roomType = 'Please select a room type.';
    return Object.keys(this.errors).length === 0;
  }

  doSearch() {
    if (!this.validate()) return;
    this.loading = true; this.serverError = '';
    this.roomService.getAvailableRooms(this.search.checkIn, this.search.checkOut, this.search.roomType).subscribe({
      next: res => {
        this.loading = false; this.searched = true;
        this.rooms = res.data || [];
        this.applyFilters();
      },
      error: err => { this.loading = false; this.serverError = err.error?.message || 'Search failed.'; }
    });
  }

  applyFilters() {
    let r = [...this.rooms];
    r = r.filter(x => x.pricePerNight >= this.filterMinPrice && x.pricePerNight <= this.filterMaxPrice);
    if (this.sortBy === 'priceAsc') r.sort((a, b) => a.pricePerNight - b.pricePerNight);
    else if (this.sortBy === 'priceDesc') r.sort((a, b) => b.pricePerNight - a.pricePerNight);
    this.filteredRooms = r;
  }

  bookNow(room: Room) {
    this.router.navigate(['/customer/book-room', room.id], {
      state: { room, search: this.search }
    });
  }
}
