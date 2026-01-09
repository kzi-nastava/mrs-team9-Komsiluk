import { Component, signal, Input, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FavoriteRouteService } from '../services/favorite-route.service';
import { FavoriteRouteResponseDTO } from '../../../../../../shared/models/favorite-route.models';
import { AuthService } from '../../../../../auth/services/auth.service';
import { ToastService } from '../../../../../../shared/components/toast/toast.service';
import { FavoriteDetailsModalService } from '../../../../../../shared/components/modal-shell/services/favorite-details-modal.service';
import { forkJoin, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { ProfileService } from '../../../../../../features/profile/services/profile.service';
import { FavoritesBusService } from '../services/favorites-bus.service';

@Component({
  selector: 'app-favorite-rides-panel',
  imports: [CommonModule],
  templateUrl: './favorite-rides-panel.component.html',
  styleUrl: './favorite-rides-panel.component.css',
})
export class FavoriteRidesPanelComponent {
  loading = signal(false);
  favorites = signal<FavoriteRouteResponseDTO[]>([]);
  @Input() open = false;

  constructor(
    private api: FavoriteRouteService,
    private auth: AuthService,
    private toast: ToastService,
    public favDetailsModal: FavoriteDetailsModalService,
    private profileService: ProfileService,
    private favBus: FavoritesBusService
  ) {
    this.favBus.refresh$.subscribe(() => this.load());
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['open']?.currentValue === true) {
      this.load();
    }
  }

  load() {
    const userId = this.auth.userId();
    if (!userId) return;

    this.loading.set(true);
    this.api.getFavorites(+userId).subscribe({
      next: (list) => {
        this.favorites.set(list ?? []);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
        this.toast.show('Failed to load favourites.');
      }
    });
  }

  calcPrice(vehicleType: string, km: number) {
    const base = vehicleType === 'STANDARD' ? 150 : vehicleType === 'LUXURY' ? 250 : 300;
    const perKm = vehicleType === 'STANDARD' ? 80 : vehicleType === 'LUXURY' ? 120 : 140;
    return Math.round(base + km * perKm);
  }

  openCard(f: FavoriteRouteResponseDTO) {
    const ids = (f.passengerIds ?? []).filter(x => x != null);

    if (ids.length === 0) {
      this.favDetailsModal.openModal({ favorite: f, passengerEmails: [] });
      return;
    }

    forkJoin(
      ids.map(id =>
        this.profileService.getProfileById(+id).pipe(
          map(p => p?.email ?? `user#${id}`),
          catchError(() => of(`user#${id}`))
        )
      )
    ).subscribe(emails => {
      const cleaned = (emails ?? [])
        .map(e => String(e).trim())
        .filter(Boolean);

      this.favDetailsModal.openModal({ favorite: f, passengerEmails: cleaned });
    });
  }
}