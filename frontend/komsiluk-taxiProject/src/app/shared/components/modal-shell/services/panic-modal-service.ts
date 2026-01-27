import { Injectable, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class PanicModalService {
  open = signal(false);
  rideId = signal<number | null>(null);

  openModal(rideId: number) {
    this.rideId.set(rideId);
    this.open.set(true);
  }

  close() {
    this.open.set(false);
    this.rideId.set(null);
  }
}