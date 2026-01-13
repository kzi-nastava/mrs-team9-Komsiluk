import { Injectable, signal, computed } from '@angular/core';

export interface LoginRequiredViewModel {
  title?: string;
  message?: string;
  confirmText?: string;
  cancelText?: string;
}

@Injectable({ providedIn: 'root' })
export class LoginRequiredModalService {
  private openSig = signal(false);
  private vmSig = signal<LoginRequiredViewModel | null>(null);

  private confirmHandler: (() => void) | null = null;

  open(vm?: LoginRequiredViewModel, onConfirm?: () => void) {
    this.vmSig.set(vm ?? null);
    this.confirmHandler = onConfirm ?? null;
    this.openSig.set(true);
  }

  close() {
    this.openSig.set(false);
    this.confirmHandler = null;
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
