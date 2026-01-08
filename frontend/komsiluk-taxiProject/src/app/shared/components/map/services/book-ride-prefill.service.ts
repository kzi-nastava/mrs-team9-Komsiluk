import { Injectable, signal, computed } from '@angular/core';
import { FavoriteRouteResponseDTO } from '../../../models/favorite-route.models';

export interface BookRidePrefillPayload {
  favorite: FavoriteRouteResponseDTO;
  passengerEmails: string[];
}

@Injectable({ providedIn: 'root' })
export class BookRidePrefillService {

  constructor() {}

  private pendingSig = signal<BookRidePrefillPayload | null>(null);
  pending = computed(() => this.pendingSig());

  request(data: BookRidePrefillPayload) {
    this.pendingSig.set(data);
  }

  clear() {
    this.pendingSig.set(null);
  }
}
