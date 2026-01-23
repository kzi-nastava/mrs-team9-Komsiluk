import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { catchError, EMPTY, Observable, switchMap, tap, throwError } from 'rxjs';
import { RideCreateDTO, RideResponseDTO } from '../../../../../../shared/models/ride.models';
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
        tap((notifs) => this.showToastsForInitiator(notifs))
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
        tap((notifs) => this.showToastsForInitiator(notifs))
      )
      .subscribe();
  }

  private showToastsForInitiator(notifs: any[]) {
    if (!notifs?.length) {
      this.toast.show('No notification received from server.');
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
        tap((notifs) => this.showToastsForInitiator(notifs)),
        catchError((err) => {
          this.toast.show('Failed to activate PANIC button.');
          return EMPTY;
        })
      )
      .subscribe();
  }
}
