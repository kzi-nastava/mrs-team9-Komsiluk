import { Component, input, output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminLiveRideCard } from '../../../shared/models/admin-ride-view.models';
@Component({
  selector: 'app-admin-live-ride-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './admin-live-ride-card.component.html',
  styleUrls: ['./admin-live-ride-card.component.css']
})
export class AdminLiveRideCardComponent {
  ride = input.required<AdminLiveRideCard>();
  selected = input<boolean>(false);
  details = output<string>();

  onDetailsClick(event: MouseEvent) {
  event.stopPropagation();
  
  const rideId = this.ride().id;
  console.log('Emitujem ID za detalje:', rideId);
  
  this.details.emit(rideId);
}

}