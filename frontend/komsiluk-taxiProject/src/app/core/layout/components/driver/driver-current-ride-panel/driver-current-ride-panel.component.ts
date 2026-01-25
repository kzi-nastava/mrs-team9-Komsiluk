import { Component, signal, computed, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { catchError, finalize, of, from, map, concatMap, toArray } from 'rxjs';
import { ChangeDetectorRef } from '@angular/core';
import { AuthService } from '../../../../auth/services/auth.service';
import { ToastService } from '../../../../../shared/components/toast/toast.service';
import { RideService } from '../../passenger/book_ride/services/ride.service';
import { DriverStartRideConfirmModalService } from '../../../../../shared/components/modal-shell/services/driver-start-ride-confirm-modal.service';
import { RideResponseDTO } from '../../../../../shared/models/ride.models';
import { GeocodingService } from '../../../../../shared/components/map/services/geocoding.service';
import { MapFacadeService } from '../../../../../shared/components/map/services/map-facade.service';
import { InconsistencyReportModalComponent } from '../../../../../features/ride/components/driver-rating-modal/inconsistency-report-modal/inconsistency-report-modal.component';

type Waypoint = { lat: number; lon: number; label?: string };

@Component({
  selector: 'app-driver-current-ride-panel',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule,InconsistencyReportModalComponent],
  templateUrl: './driver-current-ride-panel.component.html',
  styleUrl: './driver-current-ride-panel.component.css',
})
export class DriverCurrentRidePanelComponent implements OnInit {
  showReportModal = signal(false);
  loading = signal(false);
  ride = signal<RideResponseDTO | null>(null);

  form: FormGroup;

  hasRide = computed(() => !!this.ride());
  isActive = computed(() => (this.ride()?.status ?? '') === 'ACTIVE');
  isScheduledLike = computed(() => {
    const s = this.ride()?.status ?? '';
    return s === 'SCHEDULED' || s === 'ASSIGNED';
  });

  private lastRideIdForDriveTo: number | null = null;

  private pickupCoordCache: { rideId: number; lat: number; lon: number } | null = null;

  

  constructor(
    private fb: FormBuilder,
    private auth: AuthService,
    private api: RideService,
    private toast: ToastService,
    private startModal: DriverStartRideConfirmModalService,
    private cdr: ChangeDetectorRef,
    private geo: GeocodingService,
    private mapFacade: MapFacadeService,
  ) {
    this.form = this.fb.group({
      pickup: [{ value: '', disabled: true }],
      stations: this.fb.array([]),
      destination: [{ value: '', disabled: true }],
    });
  }

  get stationsArr(): FormArray {
    return this.form.get('stations') as FormArray;
  }

  ngOnInit(): void {
    this.refresh();
  }

  refresh() {
    const driverId = Number(this.auth.userId());
    if (!driverId) return;

    this.loading.set(true);

    this.api.getDriverCurrentRide(driverId).pipe(
      catchError(() => of(null)),
      finalize(() => this.loading.set(false))
    ).subscribe((resp: any) => {
      if (!resp || resp.status === 204) {
        this.ride.set(null);
        this.fillForm(null);

        this.lastRideIdForDriveTo = null;
        this.pickupCoordCache = null;

        this.mapFacade.clearDriveTo?.();
        (this.mapFacade as any).clearRidePath?.();
        this.mapFacade.setState?.(null);

        return;
      }

      const dto = resp.body as RideResponseDTO;
      this.ride.set(dto);
      this.fillForm(dto);

      this.maybeDriveToPickup(dto);
    });
  }

  private asText(v: any): string {
    if (!v) return '';
    if (typeof v === 'string') return v;
    return v.address || v.name || '';
  }

  private fillForm(dto: RideResponseDTO | null) {
    const pickup = dto ? this.asText(dto.startAddress) : '';
    const dest = dto ? this.asText(dto.endAddress) : '';

    this.form.patchValue({ pickup, destination: dest }, { emitEvent: false });

    while (this.stationsArr.length) this.stationsArr.removeAt(0);

    const stations = dto?.stops ?? [];
    for (const s of stations) {
      this.stationsArr.push(this.fb.control({ value: this.asText(s), disabled: true }));
    }

    queueMicrotask(() => this.cdr.detectChanges());
  }

  private maybeDriveToPickup(dto: RideResponseDTO) {
    const status = dto?.status ?? '';
    if (status !== 'ASSIGNED' && status !== 'SCHEDULED') return;

    if (this.lastRideIdForDriveTo === dto.id) return;
    this.lastRideIdForDriveTo = dto.id;

    const pickupText = this.asText(dto.startAddress);
    if (!pickupText) return;

    const driverId = Number(this.auth.userId());
    if (!driverId) return;

    this.geo.lookupOne(pickupText).pipe(
      catchError(() => of(null))
    ).subscribe((res: any) => {
      if (!res) return;

      const lat = Number(res.lat);
      const lon = Number(res.lon);
      if (!Number.isFinite(lat) || !Number.isFinite(lon)) return;

      // ✅ cache za startRide (da ne ponavlja geocoding pickup-a)
      this.pickupCoordCache = { rideId: dto.id, lat, lon };

      this.mapFacade.setDriveTo(driverId, lat, lon);
    });
  }

  openStartConfirm() {
    const r = this.ride();
    if (!r) return;

    this.startModal.openModal({ rideId: r.id }, (rideId) => {
      this.doStart(rideId);
    });
  }

  private doStart(rideId: number) {
    this.loading.set(true);

    this.api.startRide(rideId).pipe(
      finalize(() => this.loading.set(false))
    ).subscribe({
      next: (updated) => {
        this.ride.set(updated);
        this.fillForm(updated);

        this.mapFacade.clearDriveTo?.();

        if ((updated?.status ?? '') === 'ACTIVE') {
          this.buildRidePath(updated).subscribe((points) => {
            if (!points) {
              this.toast.show('Could not build ride path (geocoding failed).');
              return;
            }
            (this.mapFacade as any).setRidePath?.(points);
          });
        }
      },
      error: () => {
        this.toast.show('Could not start ride.');
      }
    });
  }

  private buildRidePath(dto: RideResponseDTO) {
    const pickupText = this.asText(dto.startAddress);
    const destText = this.asText(dto.endAddress);
    const stopsText = (dto.stops ?? []).map(s => this.asText(s)).filter(Boolean);

    const textsInOrder = [pickupText, ...stopsText, destText].filter(Boolean);
    if (textsInOrder.length < 2) return of<Waypoint[] | null>(null);

    const canUsePickupCache =
      this.pickupCoordCache && this.pickupCoordCache.rideId === dto.id;

    return from(textsInOrder).pipe(
      concatMap((txt, idx) => {
        if (idx === 0 && canUsePickupCache) {
          return of<Waypoint>({
            lat: this.pickupCoordCache!.lat,
            lon: this.pickupCoordCache!.lon,
            label: 'Pickup'
          });
        }

        return this.geo.lookupOne(txt).pipe(
          map((res: any) => {
            if (!res) return null;

            const lat = Number(res.lat);
            const lon = Number(res.lon);
            if (!Number.isFinite(lat) || !Number.isFinite(lon)) return null;

            let label: string | undefined;
            if (idx === 0) label = 'Pickup';
            else if (idx === textsInOrder.length - 1) label = 'Destination';
            else label = `Stop ${idx}`;

            return { lat, lon, label } as Waypoint;
          }),
          catchError(() => of(null))
        );
      }),
      toArray(),
      map(arr => arr.filter(Boolean) as Waypoint[]),
      map(points => (points.length >= 2 ? points : null))
    );
  }

  cancel() {
    this.toast.show('Not implemented.');
  }

// driver-current-ride-panel.component.ts

finish() {
  const currentRide = this.ride();
  if (!currentRide) return;

  this.loading.set(true);

  this.api.finishRide(currentRide.id).pipe(
    finalize(() => this.loading.set(false))
  ).subscribe({
    next: (updatedRide) => {
      this.toast.show('Ride finished successfully.');

      // 1. Ažuriraj lokalni status
      this.ride.set(updatedRide); 
      this.fillForm(updatedRide);

      // 2. KLJUČNO: Pozovi čišćenje fasade
      // Ovo će setovati signale na null, što će trigerovati Effect u mapi 
      // da obriše sve Layer-e (markere i linije) povezane sa tom vožnjom.
      this.mapFacade.clearFocusRide();
      
      // Opciono: ako tvoja mapa koristi odvojenu metodu za putanju:
      this.mapFacade.clearRidePath?.();

      setTimeout(() => this.refresh(), 2000);
    },
    error: (err) => {
      this.toast.show('Error while finishing the ride.');
    }
  });
}
  stop() {
    this.toast.show('Not implemented.');
  }

  panic() {
    this.toast.show('Not implemented.');
  }
  reportInconsistency() {
  this.showReportModal.set(true);
}
}
