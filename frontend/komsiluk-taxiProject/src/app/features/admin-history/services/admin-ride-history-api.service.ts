import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export type AdminRideSortBy = 
  | 'DATE' 
  | 'PRICE' 
  | 'ROUTE' 
  | 'START_TIME' 
  | 'END_TIME' 
  | 'START_ADDRESS' 
  | 'END_ADDRESS' 
  | 'CANCELLED' 
  | 'CANCELLED_BY' 
  | 'PANIC';


export type CancellationSource = 'PASSENGER' | 'DRIVER';

export interface AdminRideHistoryDTO {
  rideId: number;
  startAddress: string;
  endAddress: string;
  startTime: string;
  endTime: string;
  cancellationSource: CancellationSource | null;
  cancellationReason: string | null;
  price: number;
  panicTriggered: boolean;
  route: string;
}

export interface AdminRideDetailsDTO {
  rideId: number;
  status: string;
  route: RouteResponseDTO;
  createdAt: string;
  scheduledAt: string | null;
  startTime: string;
  endTime: string;
  price: number;
  
  driver: DriverResponseDTO | null;
  passengerIds: number[];
  passengerEmails: string[];
  creatorId: number;
  creatorEmail: string;
  
  canceled: boolean;
  cancellationSource: string | null;
  cancellationReason: string | null;
  
  panicTriggered: boolean;
  vehicleType: string;
  babyFriendly: boolean;
  petFriendly: boolean;
  distanceKm: number;
  estimatedDurationMin: number;
  
  ratings: RatingResponseDTO[];
  inconsistencyReports: InconsistencyReportResponseDTO[];
}

export interface RouteResponseDTO {
  id: number;
  startAddress: string;
  endAddress: string;
  stops: string;
}

export interface DriverResponseDTO {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  profileImageUrl: string | null;
  averageRating: number | null;
}

export interface RatingResponseDTO {
  id: number;
  rideId: number;
  raterId: number;
  raterMail: string;
  driverId: number;
  vehicleId: number;
  vehicleGrade: number;
  driverGrade: number;
  comment: string;
  createdAt: string;
}

export interface InconsistencyReportResponseDTO {
  id: number;
  rideId: number;
  reporterId: number;
  reporterEmail: string;
  message: string;
  createdAt: string;
}

@Injectable({ providedIn: 'root' })
export class AdminRideHistoryApiService {
  private readonly API_BASE = 'http://localhost:8081/api';

  constructor(private http: HttpClient) {}

  /**
   * Get rides for a user by their email
   * Endpoint: GET /api/admin/rides/by-user-email?email=...
   */
  getRidesByEmail(
    email: string,
    from?: string,
    to?: string,
    sortBy?: AdminRideSortBy
  ): Observable<AdminRideHistoryDTO[]> {
    let params = new HttpParams().set('email', email);
    if (from?.trim()) params = params.set('from', from);
    if (to?.trim()) params = params.set('to', to);
    if (sortBy) params = params.set('sortBy', sortBy);

    return this.http.get<AdminRideHistoryDTO[]>(
      `${this.API_BASE}/admin/rides/by-user-email`,
      { params }
    );
  }

  /**
   * Get detailed information about a specific ride
   * Endpoint: GET /api/rides/{rideId}
   */
  getRideDetails(rideId: number): Observable<AdminRideDetailsDTO> {
    return this.http.get<AdminRideDetailsDTO>(
      `${this.API_BASE}/rides/${rideId}`
    );
  }
}
