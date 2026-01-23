import { CommonModule } from '@angular/common';
import { Component, EventEmitter, HostListener, Input, Output } from '@angular/core';
import { RideDetailsMapComponent } from '../ride-details-map/ride-details-map.component';
export interface PassengerRating {
  email: string;
  driverRating?: number | null;
  vehicleRating?: number | null;
  comment?: string | null;
}

export interface RideHistoryDetailsVm {
  // Left
  passengers: string[];          // ovde će sada biti EMAIL-ovi
  ratings: PassengerRating[];

  // Center
  mapImageUrl: string;

  // Right
  pickupLocation: string;
  stops: string[];
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

  statusText?: string | null;             
  cancellationSource?: string | null;    
  cancellationReason?: string | null;
}

@Component({
  selector: 'app-ride-history-details-modal',
  standalone: true,
  imports: [CommonModule, RideDetailsMapComponent],
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

  isCanceled(): boolean {
    return (this.data.statusText ?? '').toUpperCase().includes('CANCEL');
  }

get allLocations(): string[] {
  if (!this.data) return [];
  
  const validStops = (this.data.stops || []).filter(s => !!s);
  
  return [
    this.data.pickupLocation,
    ...validStops,
    this.data.destination
  ];
}
}
