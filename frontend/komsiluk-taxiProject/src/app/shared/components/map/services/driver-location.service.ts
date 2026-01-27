import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, timer } from 'rxjs';
import { switchMap, shareReplay } from 'rxjs/operators';
import { DriverBasicDto, DriverLocation } from '../../../models/driver-location.model';

@Injectable({ providedIn: 'root' })
export class DriverLocationService {
  private readonly API = 'http://localhost:8081/api/drivers';

  constructor(private http: HttpClient) {}

  getLocationsOnce(): Observable<DriverLocation[]> {
    return this.http.get<DriverLocation[]>(`${this.API}/locations`);
  }

  // refresh time 1s = 1000ms
  pollLocations(): Observable<DriverLocation[]> {
    return timer(0, 1000).pipe(
      switchMap(() => this.getLocationsOnce()),
      shareReplay({ bufferSize: 1, refCount: true })
    );
  }
  getDriversBasic() {
  return this.http.get<DriverBasicDto[]>('http://localhost:8081/api/drivers/basic');
}

updateLocation(driverId: number, lat: number, lng: number) {
  return this.http.put<void>(`${this.API}/${driverId}/location`, { lat, lng });
}

}
