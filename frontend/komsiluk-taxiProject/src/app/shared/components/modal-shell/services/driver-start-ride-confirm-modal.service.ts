import { Injectable, signal, computed } from '@angular/core';

export interface DriverStartRideConfirmModalData {
  rideId: number;
}

@Injectable({ providedIn: 'root' })
export class DriverStartRideConfirmModalService {
  private openSig = signal(false);
  open = computed(() => this.openSig());

  private dataSig = signal<DriverStartRideConfirmModalData | null>(null);
  data = computed(() => this.dataSig());

  private onConfirm: ((rideId: number) => void) | null = null;

  openModal(data: DriverStartRideConfirmModalData, onConfirm?: (rideId: number) => void) {
    this.dataSig.set(data);
    this.onConfirm = onConfirm ?? null;
    this.openSig.set(true);
  }

  close() {
    this.openSig.set(false);
    this.dataSig.set(null);
    this.onConfirm = null;
  }

  confirm() {
    const d = this.dataSig();
    if (!d) return;
    this.onConfirm?.(d.rideId);
    this.close();
  }
}
