import { Injectable, signal, computed } from '@angular/core';

export interface BlockUserConfirmModalData {
  email: string;
}

@Injectable({ providedIn: 'root' })
export class BlockUserConfirmModalService {
  private openSig = signal(false);
  open = computed(() => this.openSig());

  private dataSig = signal<BlockUserConfirmModalData | null>(null);
  data = computed(() => this.dataSig());

  private onConfirm: ((reason: string) => void) | null = null;

  openModal(data: BlockUserConfirmModalData, onConfirm?: (reason: string) => void) {
    this.dataSig.set(data);
    this.onConfirm = onConfirm ?? null;
    this.openSig.set(true);
  }

  close() {
    this.openSig.set(false);
    this.dataSig.set(null);
    this.onConfirm = null;
  }

  confirm(reason: string) {
    this.onConfirm?.(reason);
    this.close();
  }
}
