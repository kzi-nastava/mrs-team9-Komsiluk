import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RideCreateDTO, RideResponseDTO } from '../../../../../../shared/models/ride.models';
@Injectable({ providedIn: 'root' })
export class RideApiService {
  private readonly API = 'http://localhost:8081/api/rides';

  constructor(private http: HttpClient) {}

  orderRide(dto: RideCreateDTO): Observable<RideResponseDTO> {
    return this.http.post<RideResponseDTO>(`${this.API}`, dto);
  }
}
