import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface RatingCreateDTO {
  driverGrade: number;
  vehicleGrade: number;
  comment: string;
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

@Injectable({ providedIn: 'root' })
export class RatingService {
  private readonly API = '/api/rides';

  constructor(private http: HttpClient) {}

  createRating(rideId: number, rating: RatingCreateDTO): Observable<RatingResponseDTO> {
    return this.http.post<RatingResponseDTO>(`${this.API}/${rideId}/ratings`, rating);
  }

  getRatingsForRide(rideId: number) {
  return this.http.get<RatingResponseDTO[]>(
    `http://localhost:8081/api/rides/${rideId}/ratings`
  );
}


  getRatingForRideByRater(rideId: number, raterId: number): Observable<RatingResponseDTO> {
    return this.http.get<RatingResponseDTO>(`${this.API}/${rideId}/ratings/${raterId}`);
  }
}