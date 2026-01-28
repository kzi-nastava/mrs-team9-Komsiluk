import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

export type RideStatus = 'completed' | 'canceled' | 'in-progress';

export interface RideHistoryCard {
  id: string;

  date: string;       
  startTime: string;   
  endTime: string;     

  pickup: string;
  destination: string;

  status: RideStatus;

  passengers: number;  
  kilometers: number; 
  durationText: string; 
  price: number;       

  mapImageUrl: string;
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
