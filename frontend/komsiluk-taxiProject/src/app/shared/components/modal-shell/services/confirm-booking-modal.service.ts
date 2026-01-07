import { Injectable, signal, computed } from '@angular/core';

export interface ConfirmBookingViewModel {
  km: number;
  minutes: number;
  price: number;
}

@Injectable({ providedIn: 'root' })
export class ConfirmBookingModalService {
  private openSig = signal(false);
  private vmSig = signal<ConfirmBookingViewModel | null>(null);

  private confirmHandler: (() => void) | null = null;

  open(vm: ConfirmBookingViewModel, onConfirm?: () => void) {
    this.vmSig.set(vm);
    this.confirmHandler = onConfirm ?? null;
    this.openSig.set(true);
  }

  close() {
    this.openSig.set(false);
  }

  confirm() {
    try {
      this.confirmHandler?.();
    } finally {
      this.close();
    }
  }

  isOpen = computed(() => this.openSig());
  vm = computed(() => this.vmSig());
}