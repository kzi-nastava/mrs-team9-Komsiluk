import { Injectable, signal, computed } from '@angular/core';
import { FavoriteRouteResponseDTO } from '../../../models/favorite-route.models'; // prilagodi putanju modela

export interface FavoriteDetailsModalData {
  favorite: FavoriteRouteResponseDTO;
  passengerEmails?: string[];
}

@Injectable({ providedIn: 'root' })
export class FavoriteDetailsModalService {
  private openSig = signal(false);
  open = computed(() => this.openSig());

  private dataSig = signal<FavoriteDetailsModalData | null>(null);
  data = computed(() => this.dataSig());

  openModal(data: FavoriteDetailsModalData) {
    this.dataSig.set(data);
    this.openSig.set(true);
  }

  close() {
    this.openSig.set(false);
    this.dataSig.set(null);
  }
}
