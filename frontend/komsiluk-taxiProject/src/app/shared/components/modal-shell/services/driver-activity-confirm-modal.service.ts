import { Injectable, signal, computed } from '@angular/core';

export type DriverActivityConfirmMode = 'TO_ACTIVE' | 'TO_INACTIVE';

@Injectable({ providedIn: 'root' })
export class DriverActivityConfirmModalService {
  private openSig = signal(false);
  open = computed(() => this.openSig());

  private modeSig = signal<DriverActivityConfirmMode>('TO_INACTIVE');
  mode = computed(() => this.modeSig());

  private onConfirm: (() => void) | null = null;

  openModal(mode: DriverActivityConfirmMode, onConfirm?: () => void) {
    this.modeSig.set(mode);
    this.onConfirm = onConfirm ?? null;
    this.openSig.set(true);
  }

  close() {
    this.openSig.set(false);
    this.onConfirm = null;
  }

  confirm() {
    this.onConfirm?.();
    this.close();
  }
}
