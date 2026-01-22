import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-driver-left-menu',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './driver-left-menu.component.html',
  styleUrls: ['./driver-left-menu.component.css'],
})
export class DriverSidebarComponent {
  currentRideOpen = signal(false);
  scheduledRidesOpen = signal(false);

  toggle(which: 'currentRide' | 'scheduledRides') {
    if (which === 'currentRide') this.currentRideOpen.set(!this.currentRideOpen());
    if (which === 'scheduledRides') this.scheduledRidesOpen.set(!this.scheduledRidesOpen());
  }

  currentRideOpenStatus() {
    return this.currentRideOpen();
  }

  scheduledRidesOpenStatus() {
    return this.scheduledRidesOpen();
  }
}
