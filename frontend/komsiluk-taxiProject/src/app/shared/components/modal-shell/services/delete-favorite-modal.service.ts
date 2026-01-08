import { Injectable, signal, computed } from '@angular/core';

export interface DeleteFavoriteModalData {
  favoriteId: number;
  title: string;
}

@Injectable({ providedIn: 'root' })
export class DeleteFavoriteModalService {
  private openSig = signal(false);
  open = computed(() => this.openSig());

  private dataSig = signal<DeleteFavoriteModalData | null>(null);
  data = computed(() => this.dataSig());

  private onConfirm: (() => void) | null = null;

  openModal(data: DeleteFavoriteModalData, onConfirm?: () => void) {
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
    this.onConfirm?.();
    this.close();
  }
}
