import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export type DriverStatus = 'ACTIVE' | 'INACTIVE' | 'IN_RIDE';

type DriverStatusUpdateDTO = { status: DriverStatus };

@Injectable({ providedIn: 'root' })
export class DriverService {
  private readonly API = 'http://localhost:8081/api/drivers';

  constructor(private http: HttpClient) {}

  updateStatus(driverId: number, status: DriverStatus): Observable<any> {
    const body: DriverStatusUpdateDTO = { status };
    return this.http.put(`${this.API}/${driverId}/status`, body);
  }
}
