import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

export type RideStatus = 'completed' | 'canceled' | 'in-progress';

export interface RideHistoryCard {
  id: string;

  date: string;        // "20.01.2026"
  startTime: string;   // "01:56"
  endTime: string;     // "01:58"

  pickup: string;
  destination: string;

  status: RideStatus;

  passengers: number;  // broj putnika
  kilometers: number;  // npr 5
  durationText: string; // "10 min"
  price: number;       // npr 800

  mapImageUrl: string; // assets/taxi.png (placeholder)
}

@Component({
  selector: 'app-ride-history-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './ride-history-card.component.html',
  styleUrl: './ride-history-card.component.css',
})
export class RideHistoryCardComponent {
  @Input({ required: true }) ride!: RideHistoryCard;
  @Input() selected = false;

  // Emituje rideId (kao i ranije)
  @Output() details = new EventEmitter<string>();

  onDetailsClick(e: MouseEvent) {
    e.stopPropagation();
    this.details.emit(this.ride.id);
  }
}
