import { Injectable, signal, computed } from '@angular/core';

export interface RenameFavoriteModalData {
  favoriteId: number;
  currentTitle: string;
}

@Injectable({ providedIn: 'root' })
export class RenameFavoriteModalService {
  private openSig = signal(false);
  open = computed(() => this.openSig());

  private dataSig = signal<RenameFavoriteModalData | null>(null);
  data = computed(() => this.dataSig());

  private onConfirm: ((newTitle: string) => void) | null = null;

  openModal(data: RenameFavoriteModalData, onConfirm?: (newTitle: string) => void) {
    this.dataSig.set(data);
    this.onConfirm = onConfirm ?? null;
    this.openSig.set(true);
  }

  close() {
    this.openSig.set(false);
    this.dataSig.set(null);
    this.onConfirm = null;
  }

  confirm(newTitle: string) {
    this.onConfirm?.(newTitle);
    this.close();
  }
}
