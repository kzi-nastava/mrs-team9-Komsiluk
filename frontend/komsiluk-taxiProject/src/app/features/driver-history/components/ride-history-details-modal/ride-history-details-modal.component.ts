import { CommonModule } from '@angular/common';
import { Component, EventEmitter, HostListener, Input, Output } from '@angular/core';

export interface PassengerRating {
  email: string;
  driverRating?: number | null;
  vehicleRating?: number | null;
  comment?: string | null;
}

export interface RideHistoryDetailsVm {
  // Left
  passengers: string[];
  ratings: PassengerRating[];

  // Center
  mapImageUrl: string;

  // Right
  pickupLocation: string;
  station1?: string | null;
  station2?: string | null;
  destination: string;
  startTime: string;
  endTime: string;

  // Bottom right stats
  kilometers: number;
  durationText: string;
  price: number;

  // Bottom center
  panicPressed: boolean;
  inconsistencyReport?: string | null;
}

@Component({
  selector: 'app-ride-history-details-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './ride-history-details-modal.component.html',
  styleUrls: ['./ride-history-details-modal.component.css'],
})
export class RideHistoryDetailsModalComponent {
  @Input({ required: true }) data!: RideHistoryDetailsVm;
  @Output() close = new EventEmitter<void>();

  onBackdropClick() {
    this.close.emit();
  }

  onDialogClick(e: MouseEvent) {
    e.stopPropagation();
  }

  @HostListener('document:keydown.escape')
  onEsc() {
    this.close.emit();
  }

  fmtRating(v?: number | null) {
    return v == null ? 'N/A' : `${v} stars`;
  }

  fmtText(v?: string | null) {
    return v && v.trim().length ? v : 'N/A';
  }
}
