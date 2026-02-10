import { Injectable, signal } from '@angular/core';
import { RideResponseDTO } from '../../../../shared/models/ride.models'

@Injectable({ providedIn: 'root' })
export class CancelRideModalService {
  isOpen = signal(false);
  selectedRide = signal<RideResponseDTO | null>(null);
  userRole = signal<'driver' | 'passenger'>('passenger');

  rideCancelledTrigger = signal<RideResponseDTO | null>(null);

  open(ride: RideResponseDTO, role: 'driver' | 'passenger') {
    this.selectedRide.set(ride);
    this.userRole.set(role);
    this.isOpen.set(true);
  }

  notifyCancelled(ride: RideResponseDTO) {
    this.rideCancelledTrigger.set(ride);
    this.close();
  }

  close() {
    this.isOpen.set(false);
  }
}