import { Component, signal, OnDestroy, OnInit, ChangeDetectorRef } from '@angular/core';
import { Subscription, switchMap, catchError, of, tap, map } from 'rxjs';
import { CommonModule } from '@angular/common';
import { FormArray, FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { VehicleType } from '../../../../../../shared/models/profile.models';
import { GeocodingService, AddressSuggestion } from '../../../../../../shared/components/map/services/geocoding.service';
import { RidePlannerService } from '../../../../../../shared/components/map/services/ride-planner.service';
import { ToastService } from '../../../../../../shared/components/toast/toast.service';
import { ConfirmBookingModalService } from '../../../../../../shared/components/modal-shell/services/confirm-booking-modal.service';
import { HttpErrorResponse } from '@angular/common/http';
import { RideApiService } from '../services/ride.service';
import { RideCreateDTO } from '../../../../../../shared/models/ride.models';
import { AuthService } from '../../../../../auth/services/auth.service';
import { NotificationService } from '../../../../../../features/menu/services/notification.service';

type TimeMode = 'NOW' | 'SCHEDULED';

@Component({
  selector: 'app-passenger-book-ride-panel',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './passenger-book-ride-panel.component.html',
  styleUrl: './passenger-book-ride-panel.component.css',
})
export class PassengerBookRidePanelComponent implements OnInit, OnDestroy {
  submitted = signal(false);
  form;

  pickupSuggestions: AddressSuggestion[] = [];
  destinationSuggestions: AddressSuggestion[] = [];
  stationSuggestions: AddressSuggestion[][] = [];

  private stationSubs: Subscription[] = [];
  private subs: Subscription[] = [];

  private pickupPoint: { lat: number; lon: number; label: string } | null = null;
  private destinationPoint: { lat: number; lon: number; label: string } | null = null;
  private stationPoints: Array<{ lat: number; lon: number; label: string } | null> = [];

  constructor(private fb: FormBuilder, private geocoding: GeocodingService, public ridePlanner: RidePlannerService, private cdr: ChangeDetectorRef, private toast: ToastService, public confirmModal: ConfirmBookingModalService, private rideApi: RideApiService, private auth: AuthService, private notification: NotificationService) {
    this.form = this.fb.group({
      pickup: ['', [Validators.required]],
      destination: ['', [Validators.required]],

      stations: this.fb.array<string>([]),
      users: this.fb.array<string>([]),

      vehicleType: ['STANDARD' as VehicleType, [Validators.required]],
      petFriendly: [false],
      childSeatAvailable: [false],

      timeMode: ['NOW' as TimeMode],
      scheduledAt: [''],
    });

    this.form.get('timeMode')!.valueChanges.subscribe(mode => {
      if (mode === 'SCHEDULED') {
        if (!this.form.get('scheduledAt')!.value) {
          this.form.get('scheduledAt')!.setValue(this.timeSlots[0]?.value ?? '');
        }
      } else {
        this.form.get('scheduledAt')!.setValue('');
      }
    });
  }

  ngOnInit(): void {
    this.subs.push(
      this.form.get('vehicleType')!.valueChanges.subscribe(v => {
        if (v) this.ridePlanner.setVehicleType(v as any);
      })
    );

    this.rebindStationsAutocomplete();
  }

  ngOnDestroy(): void {
    this.subs.forEach(s => s.unsubscribe());
    this.stationSubs.forEach(s => s.unsubscribe());
  }

  private pushStopsToPlanner() {
    const stops = this.stationPoints.filter(x => !!x) as any[];
    this.ridePlanner.setStops(stops);
  }

  selectPickup(s: AddressSuggestion) {
    this.form.patchValue({ pickup: s.label }, { emitEvent: false });
    this.pickupSuggestions = [];
    this.pickupPoint = { lat: s.lat, lon: s.lon, label: s.label };
    this.ridePlanner.setPickup(this.pickupPoint);
    this.pushStopsToPlanner();
  }

  selectDestination(s: AddressSuggestion) {
    this.form.patchValue({ destination: s.label }, { emitEvent: false });
    this.destinationSuggestions = [];
    this.destinationPoint = { lat: s.lat, lon: s.lon, label: s.label };
    this.ridePlanner.setDestination(this.destinationPoint);
    this.pushStopsToPlanner();
  }

  selectStation(index: number, s: AddressSuggestion) {
    this.stations.at(index).setValue(s.label, { emitEvent: false });
    this.stationSuggestions[index] = [];
    this.stationPoints[index] = { lat: s.lat, lon: s.lon, label: s.label };
    this.pushStopsToPlanner();
  }
  searchPickup() {
    const q = (this.form.get('pickup')?.value ?? '').toString().trim();

    this.pickupPoint = null;
    this.ridePlanner.setPickup(null);

    this.geocoding.search(q).subscribe(list => {
      const uniq = new Map<string, AddressSuggestion>();
      for (const s of list) uniq.set(s.label, s);
      this.pickupSuggestions = Array.from(uniq.values());
      this.cdr.markForCheck();
    });
  }

  searchDestination() {
    const q = (this.form.get('destination')?.value ?? '').toString().trim();

    this.destinationPoint = null;
    this.ridePlanner.setDestination(null);

    this.geocoding.search(q).subscribe(list => {
      const uniq = new Map<string, AddressSuggestion>();
      for (const s of list) uniq.set(s.label, s);
      this.destinationSuggestions = Array.from(uniq.values());
      this.cdr.markForCheck();
    });
  }

  searchStation(i: number) {
    const q = (this.stations.at(i)?.value ?? '').toString().trim();

    this.stationPoints[i] = null;
    this.pushStopsToPlanner();

    this.geocoding.search(q).subscribe(list => {
      const uniq = new Map<string, AddressSuggestion>();
      for (const s of list) uniq.set(s.label, s);
      this.stationSuggestions[i] = Array.from(uniq.values());
      this.cdr.markForCheck();
    });
  }

  private rebindStationsAutocomplete() {
    const n = this.stations.length;

    while (this.stationSuggestions.length < n) this.stationSuggestions.push([]);
    while (this.stationPoints.length < n) this.stationPoints.push(null);

    while (this.stationSuggestions.length > n) this.stationSuggestions.pop();
    while (this.stationPoints.length > n) this.stationPoints.pop();
  }

  get stations(): FormArray { return this.form.get('stations') as FormArray; }
  get users(): FormArray { return this.form.get('users') as FormArray; }

  addStation(): void {
    this.stations.push(this.fb.control('', Validators.required));

    this.stationSuggestions.push([]);
    this.stationPoints.push(null);

    this.rebindStationsAutocomplete();
  }

  removeStation(i: number): void {
    this.stations.removeAt(i);

    this.stationSuggestions.splice(i, 1);
    this.stationPoints.splice(i, 1);

    this.rebindStationsAutocomplete();
    this.pushStopsToPlanner();
  }

  addUser(): void {
    this.users.push(this.fb.control('', Validators.required));
  }

  removeUser(i: number): void {
    this.users.removeAt(i);
  }

  isScheduled(): boolean {
    return this.form.get('timeMode')!.value === 'SCHEDULED';
  }

  submit(): void {
    this.submitted.set(true);
    this.form.markAllAsTouched();
    if (this.form.invalid) return;

    console.log('BOOK RIDE (GUI ONLY):', this.form.value);
  }

  timeSlots = this.buildTimeSlots();

  private buildTimeSlots(stepMin = 15, maxHours = 5) {
    const slots: { label: string; value: string }[] = [];
    const now = new Date();

    const start = new Date(now);
    start.setMinutes(Math.ceil(start.getMinutes() / stepMin) * stepMin, 0, 0);

    const end = new Date(now);
    end.setHours(end.getHours() + maxHours);
    end.setMinutes(Math.ceil(end.getMinutes() / stepMin) * stepMin, 0, 0);

    for (let t = new Date(start); t <= end; t = new Date(t.getTime() + stepMin * 60000)) {
      const hh = String(t.getHours()).padStart(2, '0');
      const mm = String(t.getMinutes()).padStart(2, '0');
      slots.push({
        label: `${hh}:${mm}`,
        value: t.toISOString(),
      });
    }
    return slots;
  }

  confirmOpen = signal(false);

  private toLocalDateTimeString(isoOrAnything: string): string {
    const d = new Date(isoOrAnything);
    const local = new Date(d.getTime() - d.getTimezoneOffset() * 60000);
    return local.toISOString().slice(0, 19);
  }

  private hasValidRoute(): boolean {
    const route = this.ridePlanner.route();
    if (!route) return false;

    if (!this.pickupPoint || !this.destinationPoint) return false;

    for (let i = 0; i < this.stations.length; i++) {
      const txt = (this.stations.at(i)?.value ?? '').toString().trim();
      if (txt.length > 0 && !this.stationPoints[i]) return false;
    }

    return true;
  }

  private buildRideDto(): RideCreateDTO {
    const creatorId = this.auth.userId();

    const timeMode = this.form.get('timeMode')!.value as TimeMode;
    const scheduledAtRaw = this.form.get('scheduledAt')!.value as string;

    const scheduledAt =
      timeMode === 'SCHEDULED' && scheduledAtRaw
        ? this.toLocalDateTimeString(scheduledAtRaw)
        : null;

    const stops = this.stationPoints
      .filter((p): p is {lat:number; lon:number; label:string} => !!p)
      .map(p => p.label);

    const passengerEmails = (this.users.value as any[])
      .map(x => (x ?? '').toString().trim())
      .filter(x => !!x);

    return {
      creatorId: Number(creatorId),
      startAddress: this.pickupPoint!.label,
      endAddress: this.destinationPoint!.label,
      stops,
      distanceKm: Number(this.ridePlanner.km() ?? 0),
      estimatedDurationMin: Number(this.ridePlanner.minutes() ?? 0),
      vehicleType: this.form.get('vehicleType')!.value as any,
      babyFriendly: !!this.form.get('childSeatAvailable')!.value,
      petFriendly: !!this.form.get('petFriendly')!.value,
      scheduledAt,
      passengerEmails,
    };
  }

  openConfirm() {
    if (!this.hasValidRoute()) {
      this.toast.show('Please select valid pickup/destination and valid stations.');
      return;
    }

    this.confirmModal.open(
      {
        km: this.ridePlanner.km(),
        minutes: this.ridePlanner.minutes(),
        price: this.ridePlanner.price(),
      },
      () => {
        this.confirmBooking();
      }
    );
  }

  private confirmBooking() {
    const dto = this.buildRideDto();

    const requestStartedAt = new Date();
    const windowStart = new Date(requestStartedAt.getTime() - 2000);

    this.rideApi.orderRide(dto).pipe(
      switchMap(() => this.notification.getUnread(dto.creatorId)),

      map((notifs) => {
        const fresh = (notifs ?? []).filter(n => {
          const created = new Date(n.createdAt);
          return created >= windowStart;
        });

        if (fresh.length > 0) return fresh;

        if ((notifs ?? []).length > 0) {
          const sorted = [...notifs].sort((a,b) => +new Date(b.createdAt) - +new Date(a.createdAt));
          return sorted.slice(0, 1);
        }

        return [];
      }),

      tap((notifsToShow) => {
        if (notifsToShow.length === 0) {
          this.toast.show('No notification received from server.');
          return;
        }

        for (const n of notifsToShow) {
          this.toast.show(n.message);
        }

        for (const n of notifsToShow) {
          this.notification.markRead(n.id, true).subscribe({ error: () => {} });
        }
      }),

      catchError((err: HttpErrorResponse) => {
        if (err.status === 404) {
          const msg = "Passenger email(s) not found.";
          this.toast.show(msg);
          return of(null);
        }

        if (err.status === 400) {
          this.toast.show('Bad request.');
          return of(null);
        }

        this.toast.show('Failed to order ride.');
        return of(null);
      })
    ).subscribe();
  }
}
