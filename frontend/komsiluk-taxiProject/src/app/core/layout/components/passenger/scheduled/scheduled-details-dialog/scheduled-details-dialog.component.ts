import { Component, EventEmitter, Input, Output, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ModalShellComponent } from '../../../../../../shared/components/modal-shell/modal-shell.component';
import { RideResponseDTO } from '../../../../../../shared/models/ride.models';
import { CancelRideModalService } from '../../../../../../shared/components/modal-shell/services/confirm-ride-modal-service';

@Component({
  selector: 'app-scheduled-details-dialog',
  standalone: true,
  imports: [CommonModule, ModalShellComponent],
  templateUrl: './scheduled-details-dialog.component.html',
  styleUrl: './scheduled-details-dialog.component.css',
})
export class ScheduledDetailsDialogComponent {

  private cancelModalSvc = inject(CancelRideModalService);
  @Input() open = false;
  @Input() ride: RideResponseDTO | null = null;
  @Input() passengerEmails: string[] = [];

  @Output() close = new EventEmitter<void>();

  get stopsList(): string[] {
    const s: any = this.ride?.stops ?? [];
    return Array.isArray(s) ? s : [];
  }

  get usersCount(): number {
    return this.ride?.passengerIds?.length ?? 0;
  }

  get stationsCount(): number {
    return this.stopsList.length;
  }

  formatTime(iso?: string | null): string {
    if (!iso) return '--';
    const d = new Date(iso);
    const hh = String(d.getHours()).padStart(2, '0');
    const mm = String(d.getMinutes()).padStart(2, '0');
    return `${hh}:${mm}`;
  }

  onCancelRide() {
    if (!this.ride) return;
    this.cancelModalSvc.open(this.ride, 'passenger');
  }
}
