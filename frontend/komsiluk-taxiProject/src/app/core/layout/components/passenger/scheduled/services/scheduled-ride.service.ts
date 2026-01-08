import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RideResponseDTO } from '../../../../../../shared/models/ride.models';

@Injectable({ providedIn: 'root' })
export class ScheduledRideService {
  private readonly API = 'http://localhost:8081/api/rides';

  constructor(private http: HttpClient) {}

  getScheduledForUser(userId: number): Observable<RideResponseDTO[]> {
    return this.http.get<RideResponseDTO[]>(`${this.API}/user/${userId}/scheduled`);
  }
}
