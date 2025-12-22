import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

export type RideStatus = 'completed' | 'canceled' | 'in-progress';

export interface RideHistoryCard{
  id: string;
  date: string;          // "13.12.2025"
  startTime: string;     // "12:00"
  endTime: string;       // "14:14"
  pickup: string;
  destination: string;
  status: RideStatus;
  passengers: number;
  kilometers: number;
  durationText: string;  // "2h 14min"
  price: number;         // 200
  mapImageUrl: string;   // npr. "assets/mock/map.png"
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

  @Output() details = new EventEmitter<string>();

  onDetailsClick(e: MouseEvent) {
    e.stopPropagation();
    this.details.emit(this.ride.id);
  }
}
