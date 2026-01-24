import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export type RideStatusBackend = 'FINISHED' | 'ACTIVE' | 'CANCELED' | 'REJECTED' | 'CREATED' | 'SCHEDULED';

export interface RideResponseDTO {
  id: number;
  createdAt: string;
  scheduledAt: string | null;
  startTime: string | null;
  endTime: string | null;

  startAddress: string;
  endAddress: string;

  distanceKm: number;
  estimatedDurationMin: number;
  price: number;

  status: RideStatusBackend;

  driverId: number | null;
  creatorId: number | null;
  passengerIds: number[];
  stops: any[];

  panicTriggered: boolean;
  vehicleType: string;
  babyFriendly: boolean;
  petFriendly: boolean;

  cancellationSource: string | null;
  cancellationReason: string | null;
}

@Injectable({ providedIn: 'root' })
export class DriverRideHistoryApiService {
  private readonly API = 'http://localhost:8081/api/drivers';

  constructor(private http: HttpClient) {}

  getDriverRideHistory(driverId: number, from?: string, to?: string): Observable<RideResponseDTO[]> {
    let params = new HttpParams();
    if (from) params = params.set('from', from); // YYYY-MM-DD
    if (to) params = params.set('to', to);       // YYYY-MM-DD

    return this.http.get<RideResponseDTO[]>(`${this.API}/${driverId}/rides/history`, { params });
  }
}
