import { Injectable, signal, computed } from '@angular/core';
import { RideResponseDTO } from '../../../models/ride.models';

export interface ScheduledDetailsModalData {
  ride: RideResponseDTO;
  passengerEmails: string[];
}

@Injectable({ providedIn: 'root' })
export class ScheduledDetailsModalService {
  private openSig = signal(false);
  open = computed(() => this.openSig());

  private dataSig = signal<ScheduledDetailsModalData | null>(null);
  data = computed(() => this.dataSig());

  openModal(data: ScheduledDetailsModalData) {
    this.dataSig.set(data);
    this.openSig.set(true);
  }

  close() {
    this.openSig.set(false);
    this.dataSig.set(null);
  }
}
