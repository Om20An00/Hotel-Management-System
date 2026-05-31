export interface User {
  id?: number;
  userId?: string;
  customerName: string;
  email: string;
  countryCode?: string;
  mobileNumber?: string;
  address?: string;
  username: string;
  role?: string;
  status?: string;
  mustChangePassword?: boolean;
}

export interface Room {
  id?: number;
  roomNumber: string;
  roomType: string;
  bedType?: string;
  pricePerNight: number;
  status: string;
  amenities?: string[];
  maxOccupancy: number;
  description?: string;
  roomSize?: number;
}

export interface Booking {
  id?: number;
  bookingId?: string;
  user?: User;
  room?: Room;
  checkInDate: string;
  checkOutDate: string;
  numberOfAdults: number;
  numberOfChildren: number;
  status?: string;
  paymentStatus?: string;
  totalAmount?: number;
  basePrice?: number;
  taxAmount?: number;
  specialRequests?: string;
  paymentMethod?: string;
  transactionId?: string;
  createdAt?: string;
}

export interface Complaint {
  id?: number;
  complaintId?: string;
  user?: User;
  assignedStaff?: User;
  category: string;
  bookingId?: string;
  title: string;
  description: string;
  contactPreference: string;
  status?: string;
  response?: string;
  resolutionNotes?: string;
  actionLog?: string;
  submittedAt?: string;
  expectedResolutionDate?: string;
  resolvedAt?: string;
}

export interface Bill {
  id?: number;
  billId?: string;
  user?: User;
  booking?: Booking;
  roomCharges?: number;
  serviceCharges?: number;
  additionalFees?: number;
  taxAmount?: number;
  discount?: number;
  totalAmount?: number;
  paymentStatus?: string;
  issuedAt?: string;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}
