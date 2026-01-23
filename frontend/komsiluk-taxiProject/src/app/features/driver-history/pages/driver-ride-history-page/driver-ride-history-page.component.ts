import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, effect, signal } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Subscription } from 'rxjs';

import {
  RideHistoryCard,
  RideHistoryCardComponent,
  RideStatus,
} from '../../components/ride-history-card/ride-history-card.component';

import { RideHistoryFilterService } from '../../services/driver-history-filter.service';

import {
  RideHistoryDetailsModalComponent,
  RideHistoryDetailsVm,
  PassengerRating,
} from '../../components/ride-history-details-modal/ride-history-details-modal.component';

import { AuthService } from '../../../../core/auth/services/auth.service';

type RideHistoryBackendDTO = {
  cancellationReason: string | null | undefined;
  cancellationSource: string | null | undefined;
  id: number;
  createdAt: string;
  startTime: string | null;
  endTime: string | null;
  scheduledAt: string | null;
  startAddress: string;
  endAddress: string;
  status: string;
  panicTriggered: boolean;
  passengerIds: number[];
  distanceKm: number;
  estimatedDurationMin: number;
  price: number;
  stops: string[];
  vehicleType: string;
  creatorEmail: string | null;
  passengerEmails: string[];

};

@Component({
  selector: 'app-driver-ride-history-page',
  standalone: true,
  imports: [CommonModule, RideHistoryCardComponent, RideHistoryDetailsModalComponent],
  templateUrl: './driver-ride-history-page.component.html',
  styleUrls: ['./driver-ride-history-page.component.css'],
})
export class DriverRideHistoryPageComponent {
  private readonly API_BASE = 'http://localhost:8081/api/drivers';

  rides: RideHistoryCard[] = [];
  filteredRides: RideHistoryCard[] = [];

  private rawById = new Map<string, RideHistoryBackendDTO>();

  detailsOpen = false;
  detailsVm: RideHistoryDetailsVm | null = null;

  loading = false;
  error: string | null = null;

  private readonly driverIdSig = signal<number | null>(null);

  constructor(
    private http: HttpClient,
    private filterSvc: RideHistoryFilterService,
    private auth: AuthService,
    private cdr: ChangeDetectorRef,             
  ) {
    this.bootstrapDriverId();

    effect((onCleanup) => {
      const driverId = this.driverIdSig();
      const { from, to } = this.filterSvc.range();

      if (!driverId) return;

      const sub = this.loadHistory(driverId, from, to);
      onCleanup(() => sub.unsubscribe());
    });
  }

  private bootstrapDriverId() {
    const trySet = () => {
      const raw = this.auth.userId();
      const id = raw ? Number(raw) : NaN;

      if (!Number.isFinite(id) || id <= 0) {
        setTimeout(trySet, 50);
        return;
      }

      this.driverIdSig.set(id);
    };

    trySet();
  }

  private loadHistory(driverId: number, from: string, to: string): Subscription {
    this.loading = true;
    this.error = null;

    let params = new HttpParams();
    if (from?.trim()) params = params.set('from', from);
    if (to?.trim()) params = params.set('to', to);

    const url = `${this.API_BASE}/${driverId}/rides/history`;

    return this.http.get<RideHistoryBackendDTO[]>(url, { params }).subscribe({
      next: (res) => {
        this.applyBackendResult(res ?? []);
        queueMicrotask(() => this.cdr.detectChanges()); 
      },
      error: (err) => {
        this.loading = false;
        console.error('History load error:', err);
        this.error = 'Greška pri učitavanju istorije vožnji.';
        this.rides = [];
        this.filteredRides = [];
        this.rawById.clear();

        queueMicrotask(() => this.cdr.detectChanges()); 
      },
    });
  }

  private applyBackendResult(list: RideHistoryBackendDTO[]) {
    this.rawById.clear();

    const mapped = list.map((dto) => {
      const card = this.mapDtoToCard(dto);
      this.rawById.set(card.id, dto);
      return card;
    });

    this.rides = mapped;
    this.filteredRides = [...mapped];
    this.loading = false;

    console.log('loaded rides=', this.rides.length, 'filtered=', this.filteredRides.length);
  }

  // -------------------- DETAILS --------------------

onDetails(id: string) {
  const card = this.rides.find((r) => r.id === id);
  const raw = this.rawById.get(id);
  if (!card || !raw) return;

  const passengers =
    raw.passengerEmails && raw.passengerEmails.length
      ? raw.passengerEmails
      : this.buildPassengers(raw.passengerIds);

  const vm: RideHistoryDetailsVm = {
    passengers,
    ratings: this.buildRatings(passengers),

    mapImageUrl: card.mapImageUrl,
    pickupLocation: card.pickup,
    
    stops: raw.stops ?? [], 
    
    destination: card.destination,
    startTime: card.startTime,
    endTime: card.endTime,

    kilometers: raw.distanceKm,
    durationText: `${raw.estimatedDurationMin} min`,
    price: raw.price,

    panicPressed: !!raw.panicTriggered,
    inconsistencyReport: null,

    statusText: raw.status,
    cancellationSource: raw.cancellationSource,
    cancellationReason: raw.cancellationReason,
  };

  this.detailsVm = vm;
  this.detailsOpen = true;
}
  closeDetails() {
    this.detailsOpen = false;
    this.detailsVm = null;
  }

  // -------------------- HELPERS --------------------

  private buildPassengers(passengerIds: number[] | null | undefined): string[] {
    if (!passengerIds || passengerIds.length === 0) return ['—'];
    return passengerIds.map((id) => `Passenger #${id}`);
  }

  private buildRatings(passengers: string[]): PassengerRating[] {
    return passengers.map((p) => ({
      email: p,
      driverRating: null,
      vehicleRating: null,
      comment: null,
    }));
  }

  private mapDtoToCard(dto: RideHistoryBackendDTO): RideHistoryCard {
    const startIso = dto.startTime ?? dto.createdAt;
    const endIso = dto.endTime ?? dto.createdAt;

    return {
      id: String(dto.id),
      date: this.formatDate(startIso),
      startTime: this.formatTime(startIso),
      endTime: this.formatTime(endIso),
      pickup: dto.startAddress ?? '—',
      destination: dto.endAddress ?? '—',
      status: this.mapStatus(dto.status),
      passengers: (dto.passengerIds ?? []).length,
      kilometers: Number(dto.distanceKm ?? 0),
      durationText: `${dto.estimatedDurationMin ?? 0} min`,
      price: Number(dto.price ?? 0),
      mapImageUrl: 'assets/taxi.png',
    };
  }

  private mapStatus(status: string): RideStatus {
    const s = (status ?? '').toUpperCase();
    if (s === 'FINISHED' || s === 'COMPLETED') return 'completed';
    if (s === 'CANCELED' || s === 'CANCELLED' || s === 'REJECTED') return 'canceled';
    return 'in-progress';
  }

  private formatDate(iso: string): string {
    const d = this.safeDate(iso);
    if (!d) return '—';
    const dd = String(d.getDate()).padStart(2, '0');
    const mm = String(d.getMonth() + 1).padStart(2, '0');
    const yyyy = String(d.getFullYear());
    return `${dd}.${mm}.${yyyy}`;
  }

  private formatTime(iso: string): string {
    const d = this.safeDate(iso);
    if (!d) return '—';
    const hh = String(d.getHours()).padStart(2, '0');
    const min = String(d.getMinutes()).padStart(2, '0');
    return `${hh}:${min}`;
  }

  private safeDate(iso: string): Date | null {
    if (!iso) return null;
    const d = new Date(iso);
    return isNaN(d.getTime()) ? null : d;
  }
}
