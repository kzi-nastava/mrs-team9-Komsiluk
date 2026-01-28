import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { catchError, EMPTY, map, Observable, of, switchMap, tap, throwError } from 'rxjs';
import { RideCreateDTO, RidePassengerActiveDTO, RideResponseDTO, StopRideRequestDTO } from '../../../../../../shared/models/ride.models';
import { ToastService } from '../../../../../../shared/components/toast/toast.service';
import { NotificationService } from '../../../../../../features/menu/services/notification.service';
import { AuthService } from '../../../../../../core/auth/services/auth.service';
import { HttpResponse } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class RideService {
  private readonly API = 'http://localhost:8081/api/rides';

  constructor(
    private http: HttpClient,
    private toast: ToastService,
    private notification: NotificationService,
    private auth: AuthService
  ) { }

  orderRide(dto: RideCreateDTO): Observable<RideResponseDTO> {
    return this.http.post<RideResponseDTO>(`${this.API}`, dto);
  }

  getDriverCurrentRide(driverId: number): Observable<HttpResponse<RideResponseDTO>> {
    return this.http.get<RideResponseDTO>(`${this.API}/driver/${driverId}/current`, {
      observe: 'response',
    });
  }

  startRide(rideId: number): Observable<RideResponseDTO> {
    return this.http.post<RideResponseDTO>(`${this.API}/${rideId}/start`, null);
  }

  cancelRidePassenger(rideId: number, reason: string): void {
    const userId = this.auth.userId();
    if (!userId) {
      this.toast.show('User not logged in.');
      return;
    }

    this.cancelRideInternal(rideId, reason, 'passenger')
      .pipe(
        switchMap(() => this.notification.getUnread(userId)),
        tap((notifs) => {
          const cancelNotifs = notifs.filter(n => n.type === 'RIDE_CANCELLED');
          this.showToastsForInitiator(cancelNotifs);
        })
      )
      .subscribe();
  }

  cancelRideDriver(rideId: number, reason: string): void {
    const userId = this.auth.userId();
    if (!userId) {
      this.toast.show('User not logged in.');
      return;
    }

    this.cancelRideInternal(rideId, reason, 'driver')
      .pipe(
        switchMap(() => this.notification.getUnread(userId)),
        tap((notifs) => {
          const cancelNotifs = notifs.filter(n => n.type === 'RIDE_CANCELLED');
          this.showToastsForInitiator(cancelNotifs);
        })
      )
      .subscribe();
  }

  private showToastsForInitiator(notifs: any[]) {
    if (!notifs?.length) {
      return;
    }

    for (const n of notifs) {
      this.toast.show(n.message);
    }

    for (const n of notifs) {
      this.notification.markRead(n.id, true).subscribe({ error: () => { } });
    }
  }


  private cancelRideInternal(
    rideId: number,
    reason: string,
    role: 'passenger' | 'driver'
  ): Observable<void> {

    if (!rideId || rideId <= 0) {
      return throwError(() => new Error('Invalid ride id.'));
    }

    const trimmedReason = (reason ?? '').trim();
    if (!trimmedReason) {
      return throwError(() => new Error('Cancellation reason is required.'));
    }

    const dto = { reason: trimmedReason };

    return this.http
      .post<void>(`${this.API}/${rideId}/cancel/${role}`, dto)
      .pipe(
        tap(() => {
          this.toast.show('Ride successfully cancelled.');
        }),

        catchError((err: HttpErrorResponse) => {
          if (err.status === 400) {
            this.toast.show('Ride cannot be cancelled in its current state.');
            return EMPTY;
          }

          if (err.status === 404) {
            this.toast.show('Ride not found.');
            return EMPTY;
          }

          this.toast.show('Failed to cancel ride.');
          return EMPTY;
        })
      );
  }

  activatePanicButton(rideId: number): void {
    const userId = this.auth.userId();
    if (!userId) {
      this.toast.show('User not logged in.');
      return;
    }

    this.http.post<void>(`${this.API}/${rideId}/panic`, { initiatorId: userId })
      .pipe(
        switchMap(() => this.notification.getUnread(userId)),
        tap((notifs) => {
          const panicNotifs = notifs.filter(n => n.type === 'PANIC');
          this.showToastsForInitiator(panicNotifs);
        }),
        catchError((err) => {
          this.toast.show('Failed to activate PANIC button.');
          return EMPTY;
        })
      )
      .subscribe();
  }

  stopRide(rideId: number, dto: StopRideRequestDTO): Observable<RideResponseDTO> {
    return this.http.post<RideResponseDTO>(`${this.API}/${rideId}/stop`, dto).pipe(
      tap(() => this.toast.show('Ride stopped and recorded.')),
      catchError(err => {
        this.toast.show('Failed to stop ride properly.');
        return throwError(() => err);
      })
    );
  }

  getScheduledRides(userId: number): Observable<RideResponseDTO[]> {
    return this.http.get<RideResponseDTO[]>(`${this.API}/user/${userId}/scheduled`);
  }
  
  finishRide(rideId: number): Observable<RideResponseDTO> {
    return this.http.post<RideResponseDTO>(`${this.API}/${rideId}/finish`, {});
  }

  reportInconsistency(rideId: number, message: string): Observable<any> {
    return this.http.post(`${this.API}/${rideId}/inconsistencies`, { message });
  }

  getInconsistencyReports(rideId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.API}/${rideId}/inconsistencies`);
  }

  getActiveRideForPassenger(): Observable<RidePassengerActiveDTO | null> {
  return this.http.get<RidePassengerActiveDTO>(`${this.API}/passenger/active`, { observe: 'response' })
    .pipe(
      map(response => response.status === 204 ? null : response.body),
      catchError(() => of(null))
    );
}
}
