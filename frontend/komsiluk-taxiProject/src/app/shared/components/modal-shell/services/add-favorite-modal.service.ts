import { Injectable, signal, computed } from '@angular/core';

export interface AddFavoriteModalData {
  defaultName?: string;
}

@Injectable({ providedIn: 'root' })
export class AddFavoriteModalService {
  private openSig = signal(false);
  open = computed(() => this.openSig());

  private dataSig = signal<AddFavoriteModalData | null>(null);
  data = computed(() => this.dataSig());

  private onConfirm: ((name: string) => void) | null = null;

  openModal(data: AddFavoriteModalData, onConfirm?: (name: string) => void) {
    this.dataSig.set(data);
    this.onConfirm = onConfirm ?? null;
    this.openSig.set(true);
  }

  close() {
    this.openSig.set(false);
    this.dataSig.set(null);
    this.onConfirm = null;
  }

  confirm(name: string) {
    this.onConfirm?.(name);
    this.close();
  }
}
