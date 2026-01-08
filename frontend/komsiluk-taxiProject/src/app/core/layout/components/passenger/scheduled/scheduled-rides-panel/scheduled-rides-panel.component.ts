import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../../../auth/services/auth.service';
import { ToastService } from '../../../../../../shared/components/toast/toast.service';
import { ScheduledRideService } from '../services/scheduled-ride.service';
import { RideResponseDTO } from '../../../../../../shared/models/ride.models';
import { ScheduledDetailsModalService } from '../../../../../../shared/components/modal-shell/services/scheduled-details-modal.service';
import { ProfileService } from '../../../../../../features/profile/services/profile.service';
import { forkJoin, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

@Component({
  selector: 'app-scheduled-rides-panel',
  imports: [CommonModule],
  templateUrl: './scheduled-rides-panel.component.html',
  styleUrl: './scheduled-rides-panel.component.css',
})
export class ScheduledRidesPanelComponent {
  loading = signal(false);
  rides = signal<RideResponseDTO[]>([]);

  constructor(
    private api: ScheduledRideService,
    private auth: AuthService,
    private toast: ToastService,
    public detailsModal: ScheduledDetailsModalService,
    private profileService: ProfileService,
  ) {}

  load() {
    const userId = this.auth.userId();
    if (!userId) return;

    this.loading.set(true);
    this.api.getScheduledForUser(+userId).subscribe({
      next: (list) => {
        this.rides.set(list ?? []);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
        this.toast.show('Failed to load scheduled rides.');
      }
    });
  }

  formatTime(iso?: string | null): string {
    if (!iso) return '--';
    const d = new Date(iso);
    const hh = String(d.getHours()).padStart(2, '0');
    const mm = String(d.getMinutes()).padStart(2, '0');
    return `${hh}:${mm}`;
  }

  openCard(r: RideResponseDTO) {
    const ids = (r.passengerIds ?? []).filter(x => x != null);

    if (ids.length === 0) {
      this.detailsModal.openModal({ ride: r, passengerEmails: [] });
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
      const cleaned = Array.from(
        new Set((emails ?? []).map(e => String(e).trim()).filter(Boolean))
      );
      this.detailsModal.openModal({ ride: r, passengerEmails: cleaned });
    });
  }
}
