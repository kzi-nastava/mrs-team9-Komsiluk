import { Component, signal, computed, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { catchError, finalize, of } from 'rxjs';
import { ChangeDetectorRef } from '@angular/core';
import { AuthService } from '../../../../auth/services/auth.service';
import { ToastService } from '../../../../../shared/components/toast/toast.service';
import { RideService } from '../../passenger/book_ride/services/ride.service';
import { DriverStartRideConfirmModalService } from '../../../../../shared/components/modal-shell/services/driver-start-ride-confirm-modal.service';
import { RideResponseDTO } from '../../../../../shared/models/ride.models';
import { GeocodingService } from '../../../../../shared/components/map/services/geocoding.service';
import { MapFacadeService } from '../../../../../shared/components/map/services/map-facade.service';

@Component({
  selector: 'app-driver-current-ride-panel',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './driver-current-ride-panel.component.html',
  styleUrl: './driver-current-ride-panel.component.css',
})
export class DriverCurrentRidePanelComponent implements OnInit {
  loading = signal(false);
  ride = signal<RideResponseDTO | null>(null);

  form: FormGroup;

  hasRide = computed(() => !!this.ride());
  isActive = computed(() => (this.ride()?.status ?? '') === 'ACTIVE');
  isScheduledLike = computed(() => {
    const s = this.ride()?.status ?? '';
    return s === 'SCHEDULED' || s === 'ASSIGNED';
  });

  // ✅ NEW: da ne geocode-uje stalno na refresh/poll
  private lastRideIdForDriveTo: number | null = null;

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

        // ✅ NEW: ocisti mapu kad nema voznje
        this.lastRideIdForDriveTo = null;
        this.mapFacade.clearDriveTo?.();
        this.mapFacade.setState?.(null);

        return;
      }

      const dto = resp.body as RideResponseDTO;
      this.ride.set(dto);
      this.fillForm(dto);

      // ✅ NEW: pre-ride faza – vozi do pickup lokacije (ASSIGNED/SCHEDULED)
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

  // ✅ NEW: kad je ride ASSIGNED/SCHEDULED – geocode pickup i posalji mapi driveTo
  private maybeDriveToPickup(dto: RideResponseDTO) {
    const status = dto?.status ?? '';
    if (status !== 'ASSIGNED' && status !== 'SCHEDULED') return;

    // samo jednom po ride-u
    if (this.lastRideIdForDriveTo === dto.id) return;
    this.lastRideIdForDriveTo = dto.id;

    const pickupText = this.asText(dto.startAddress);
    if (!pickupText) return;

    const driverId = Number(this.auth.userId());
    if (!driverId) return;

    // Nominatim lookup (lookupOne treba da vrati {lat, lon} ili null)
    this.geo.lookupOne(pickupText).pipe(
      catchError(() => of(null))
    ).subscribe((res: any) => {
      if (!res) return;

      const lat = Number(res.lat);
      const lon = Number(res.lon);
      if (!Number.isFinite(lat) || !Number.isFinite(lon)) return;

      // MapComponent slusa facade.driveTo i animira do pickup-a
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

        // ✅ NEW (opciono): kad startuje voznju, vise nam ne treba pre-ride driveTo
        this.mapFacade.clearDriveTo?.();
      },
      error: () => {
        this.toast.show('Could not start ride.');
      }
    });
  }

  cancel() {
    this.toast.show('Not implemented.');
  }

  finish() {
    this.toast.show('Not implemented.');
  }

  stop() {
    this.toast.show('Not implemented.');
  }

  panic() {
    this.toast.show('Not implemented.');
  }
}
