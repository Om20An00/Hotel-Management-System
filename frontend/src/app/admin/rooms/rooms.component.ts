import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RoomService } from '../../services/room.service';
import { Room } from '../../models/models';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-rooms',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink], // ✅ RouterLink removed
  templateUrl: './rooms.component.html'
})
export class RoomsComponent implements OnInit {
  rooms: Room[] = [];
  total = 0; page = 0; size = 10; totalPages = 0;
  query = ''; sort = 'roomNumber'; direction = 'asc';
  loading = true; error = ''; success = '';
  showAdd = false; showEdit = false; editRoom: any = null;
  newRoom: any = { status: 'AVAILABLE', amenities: [] };
  amenityInput = '';
  bulkFile: File | null = null;

  constructor(private roomService: RoomService) {}

  ngOnInit() {
    this.load();
  }

  load() {
    this.loading = true;
    this.roomService
      .adminGetAll(this.page, this.size, this.sort, this.direction, this.query)
      .subscribe({
        next: r => {
          const d = r.data;
          this.rooms = d.content;
          this.total = d.totalElements;
          this.totalPages = d.totalPages;
          this.loading = false;
        },
        error: () => this.loading = false
      });
  }

  search() { this.page = 0; this.load(); }
  sortBy(col: string) {
    this.sort = col;
    this.direction = this.direction === 'asc' ? 'desc' : 'asc';
    this.load();
  }

  addAmenity() {
    if (this.amenityInput.trim()) {
      (this.showAdd ? this.newRoom : this.editRoom)
        .amenities.push(this.amenityInput.trim());
      this.amenityInput = '';
    }
  }

  removeAmenity(list: string[], i: number) {
    list.splice(i, 1);
  }

  submitAdd() {
    this.roomService.adminAddRoom(this.newRoom).subscribe({
      next: r => {
        if (r.success) {
          this.success = 'Room added successfully.';
          this.showAdd = false;
          this.newRoom = { status: 'AVAILABLE', amenities: [] };
          this.load();
        } else this.error = r.message;
      },
      error: err => this.error = err.error?.message || 'Add failed.'
    });
  }

  openEdit(room: Room) {
    this.editRoom = { ...room, amenities: [...(room.amenities || [])] };
    this.showEdit = true;
    this.showAdd = false;
  }

  submitEdit() {
    this.roomService.adminUpdateRoom(this.editRoom.id, this.editRoom).subscribe({
      next: r => {
        if (r.success) {
          this.success =
            `Room ${this.editRoom.roomNumber} details are updated successfully.`;
          this.showEdit = false;
          this.load();
        } else this.error = r.message;
      },
      error: err => this.error = err.error?.message || 'Update failed.'
    });
  }

  onFileSelect(e: any) {
    this.bulkFile = e.target.files[0];
  }

  bulkUpload() {
    if (!this.bulkFile) {
      this.error = 'Please select a CSV file.';
      return;
    }
    this.roomService.adminBulkImport(this.bulkFile).subscribe({
      next: r => {
        this.success = r.message;
        this.load();
        this.bulkFile = null;
      },
      error: err =>
        this.error = err.error?.message || 'Bulk upload failed.'
    });
  }

  downloadTemplate() {
    const csv =
      'RoomNumber,RoomType,Price,MaxOccupancy,Status,Description\n' +
      'R001,STANDARD,2500,2,AVAILABLE,Standard room\n';
    const a = document.createElement('a');
    a.href = 'data:text/csv,' + encodeURIComponent(csv);
    a.download = 'room_template.csv';
    a.click();
  }
}