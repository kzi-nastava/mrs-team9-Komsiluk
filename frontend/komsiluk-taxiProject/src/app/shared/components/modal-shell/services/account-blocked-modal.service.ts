import { Injectable, signal, computed } from '@angular/core';

export interface AccountBlockedModalData {
  adminEmail?: string;
  reason?: string;
  createdAt?: string;
}

@Injectable({ providedIn: 'root' })
export class AccountBlockedModalService {
  private openSig = signal(false);
  open = computed(() => this.openSig());

  private dataSig = signal<AccountBlockedModalData | null>(null);
  data = computed(() => this.dataSig());

  openModal(data: AccountBlockedModalData) {
    this.dataSig.set(data);
    this.openSig.set(true);
  }

  close() {
    this.openSig.set(false);
    this.dataSig.set(null);
  }
}
