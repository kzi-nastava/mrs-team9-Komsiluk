import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

// Matches backend AdminRideSortBy enum
export type PassengerRideSortBy = 
  | 'DATE'
  | 'ROUTE' 
  | 'START_TIME' 
  | 'END_TIME' 
  | 'START_ADDRESS' 
  | 'END_ADDRESS';

// Matches backend AdminRideHistoryDTO
export interface PassengerRideHistoryDTO {
  rideId: number;
  startAddress: string;
  endAddress: string;
  startTime: string;       // LocalDateTime as ISO string
  endTime: string;         // LocalDateTime as ISO string
  route: string;
}

// Matches backend AdminRideDetailsDTO
export interface PassengerRideDetailsDTO {
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
  stops: string;  // pipe-separated string from backend
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
export class PassengerRideHistoryApiService {
  private readonly API_BASE = 'http://localhost:8081/api';

  constructor(private http: HttpClient) {}

  getRides(
    userId: number,
    from?: string,
    to?: string,
    sortBy?: PassengerRideSortBy
  ): Observable<PassengerRideHistoryDTO[]> {
    let params = new HttpParams();
    if (from?.trim()) params = params.set('from', from);
    if (to?.trim()) params = params.set('to', to);
    if (sortBy) params = params.set('sortBy', sortBy);

    return this.http.get<PassengerRideHistoryDTO[]>(
      `${this.API_BASE}/passengers/${userId}/rides`,
      { params }
    );
  }

  getRideDetails(rideId: number): Observable<PassengerRideDetailsDTO> {
    return this.http.get<PassengerRideDetailsDTO>(
      `${this.API_BASE}/rides/${rideId}`
    );
  }
}
