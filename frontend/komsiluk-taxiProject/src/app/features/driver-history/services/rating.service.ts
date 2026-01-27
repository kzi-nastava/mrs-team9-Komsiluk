import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// DTO za slanje ocene (na osnovu onoga što controller očekuje u @RequestBody)
export interface RatingCreateDTO {
  driverGrade: number;
  vehicleGrade: number;
  comment: string;
}

// DTO odgovora sa backenda (identičan tvojoj Java klasi RatingResponseDTO)
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
  createdAt: string; // LocalDateTime stiže kao string
}

@Injectable({ providedIn: 'root' })
export class RatingService {
  // Base path definisan u controlleru sa @RequestMapping("/api/rides")
  private readonly API = '/api/rides';

  constructor(private http: HttpClient) {}

  // POST /{rideId}/ratings
  createRating(rideId: number, rating: RatingCreateDTO): Observable<RatingResponseDTO> {
    return this.http.post<RatingResponseDTO>(`${this.API}/${rideId}/ratings`, rating);
  }

  getRatingsForRide(rideId: number) {
  return this.http.get<RatingResponseDTO[]>(
    `http://localhost:8081/api/rides/${rideId}/ratings`
  );
}


  // GET /{rideId}/ratings/{raterId}
  getRatingForRideByRater(rideId: number, raterId: number): Observable<RatingResponseDTO> {
    return this.http.get<RatingResponseDTO>(`${this.API}/${rideId}/ratings/${raterId}`);
  }
}