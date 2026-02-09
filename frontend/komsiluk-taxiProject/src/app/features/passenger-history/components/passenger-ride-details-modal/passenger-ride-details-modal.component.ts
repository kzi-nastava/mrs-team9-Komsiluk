import { CommonModule } from '@angular/common';
import { Component, EventEmitter, HostListener, inject, Input, Output } from '@angular/core';
import { Router } from '@angular/router';

import { RideDetailsMapComponent } from '../../../../shared/components/ride-details-map/ride-details-map.component';
import { PassengerRideDetailsDTO } from '../../services/passenger-ride-history-api.service';
import { BookRidePrefillService } from '../../../../shared/components/map/services/book-ride-prefill.service';
import { FavoriteRouteResponseDTO, FavoriteRouteCreateDTO } from '../../../../shared/models/favorite-route.models';
import { AddFavoriteModalService } from '../../../../shared/components/modal-shell/services/add-favorite-modal.service';
import { FavoriteRouteService } from '../../../../core/layout/components/passenger/favorite/services/favorite-route.service';
import { RouteService } from '../../../../core/layout/components/passenger/favorite/services/route.service';
import { AuthService } from '../../../../core/auth/services/auth.service';
import { ToastService } from '../../../../shared/components/toast/toast.service';
import { RouteCreateDTO } from '../../../../shared/models/route.models';
import { switchMap, finalize } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-passenger-ride-details-modal',
  standalone: true,
  imports: [CommonModule, RideDetailsMapComponent],
  templateUrl: './passenger-ride-details-modal.component.html',
  styleUrls: ['./passenger-ride-details-modal.component.css'],
})
export class PassengerRideDetailsModalComponent {
  @Input() details: PassengerRideDetailsDTO | null = null;
  @Input() loading = false;
  @Output() close = new EventEmitter<void>();

  private prefillService = inject(BookRidePrefillService);
  private router = inject(Router);
  private addFavModal = inject(AddFavoriteModalService);
  private favoriteRouteApi = inject(FavoriteRouteService);
  private routeApi = inject(RouteService);
  private auth = inject(AuthService);
  private toast = inject(ToastService);

  private readonly IMG_BASE = 'http://localhost:8081';
  saving = false;

  onBackdropClick(): void {
    this.close.emit();
  }

  onDialogClick(e: MouseEvent): void {
    e.stopPropagation();
  }

  @HostListener('document:keydown.escape')
  onEsc(): void {
    this.close.emit();
  }

  get allLocations(): string[] {
    if (!this.details?.route) return [];
    // Helper to extract street name
    const street = (addr: string) => typeof addr === 'string' ? addr.split(',')[0].trim() : '';
    const stopsArr = this.stopsArray;
    return [
      street(this.details.route.startAddress),
      ...stopsArr,
      street(this.details.route.endAddress)
    ];
  }

  get stopsArray(): string[] {
    if (!this.details?.route) return [];
    const street = (addr: string) => typeof addr === 'string' ? addr.split(',')[0].trim() : '';
    const rawStops: any = this.details.route.stops;
    if (rawStops == null) {
      return [];
    } else if (Array.isArray(rawStops)) {
      return rawStops.filter((s: string) => !!s).map(street);
    } else if (typeof rawStops === 'string') {
      return rawStops.split('|').filter((s: string) => !!s.trim()).map((s: string) => street(s));
    }
    return [];
  }

  get driverFullName(): string {
    if (!this.details?.driver) return 'N/A';
    return `${this.details.driver.firstName} ${this.details.driver.lastName}`;
  }

  get driverProfilePicture(): string | null {
    const url = this.details?.driver?.profileImageUrl?.trim();
    if (!url) return null;
    return `${this.IMG_BASE}${url}`;
  }

  get driverRating(): string {
    if (!this.details?.driver?.averageRating) return 'N/A';
    return `${this.details.driver.averageRating.toFixed(1)} ★`;
  }

  isCanceled(): boolean {
    return this.details?.cancellationSource != null;
  }

  formatDateTime(isoString: string | null | undefined): string {
    if (!isoString) return '—';
    const d = new Date(isoString);
    if (isNaN(d.getTime())) return '—';
    
    const dd = String(d.getDate()).padStart(2, '0');
    const mm = String(d.getMonth() + 1).padStart(2, '0');
    const yyyy = d.getFullYear();
    const hh = String(d.getHours()).padStart(2, '0');
    const min = String(d.getMinutes()).padStart(2, '0');
    
    return `${dd}.${mm}.${yyyy} ${hh}:${min}`;
  }

  fmtRating(v?: number | null): string {
    return v == null ? 'N/A' : `${v} ★`;
  }

  fmtText(v?: string | null): string {
    return v && v.trim().length ? v : 'N/A';
  }

  // === Actions ===

  onOrderAgain(): void {
    if (!this.details?.route) return;

    const d = this.details;
    
    // Parse stops - backend returns pipe-separated string
    const stopsArr = this.stopsArray;

    // Map to FavoriteRouteResponseDTO format for prefill service
    const fakeFavorite: FavoriteRouteResponseDTO = {
      id: 0, // Not used for prefill
      title: 'Order Again',
      routeId: d.route.id,
      startAddress: d.route.startAddress,
      endAddress: d.route.endAddress,
      stops: stopsArr,
      passengerIds: d.passengerIds ?? [],
      vehicleType: (d.vehicleType as any) ?? 'STANDARD',
      petFriendly: d.petFriendly ?? false,
      babyFriendly: d.babyFriendly ?? false,
      distanceKm: d.distanceKm,
      estimatedDurationMin: d.estimatedDurationMin
    };

    this.prefillService.request({
      favorite: fakeFavorite,
      passengerEmails: d.passengerEmails ?? []
    });

    this.close.emit();
    this.router.navigate(['/']);
  }

  onSaveAsFavorite(): void {
    if (!this.details?.route) return;
    
    const d = this.details;
    const defaultName = `${d.route.startAddress} → ${d.route.endAddress}`;
    
    this.addFavModal.openModal(
      { defaultName },
      (title: string) => this.confirmSaveAsFavorite(title)
    );
  }

  private confirmSaveAsFavorite(title: string): void {
    const userId = this.auth.userId();
    if (!userId) {
      this.toast.show('Not logged in.');
      return;
    }

    if (!this.details?.route) return;
    
    const d = this.details;
    const stopsStr = this.stopsArray.join('|');

    const routeDto: RouteCreateDTO = {
      startAddress: d.route.startAddress,
      endAddress: d.route.endAddress,
      stops: stopsStr,
      distanceKm: d.distanceKm,
      estimatedDurationMin: d.estimatedDurationMin,
    };

    const favDtoBase = {
      title,
      passengersEmails: d.passengerEmails ?? [],
      vehicleType: (d.vehicleType as any) ?? 'STANDARD',
      petFriendly: d.petFriendly ?? false,
      babyFriendly: d.babyFriendly ?? false,
    };

    this.saving = true;

    this.routeApi.findOrCreate(routeDto).pipe(
      switchMap(createdRoute => {
        const favDto: FavoriteRouteCreateDTO = { ...favDtoBase, routeId: createdRoute.id };
        return this.favoriteRouteApi.addFavorite(+userId, favDto);
      }),
      finalize(() => this.saving = false)
    ).subscribe({
      next: () => {
        this.toast.show('Added to favourites!');
        this.addFavModal.close();
      },
      error: (err: HttpErrorResponse) => {
        const msg =
          err?.error?.message ||
          err?.error ||
          err?.message ||
          'Failed to add favourite.';
        this.toast.show(String(msg));
      }
    });
  }
}
