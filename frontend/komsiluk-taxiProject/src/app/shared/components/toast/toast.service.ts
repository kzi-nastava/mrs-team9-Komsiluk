import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export type ToastType = 'success' | 'error';

export interface ToastState {
  message: string;
  type: ToastType;
}

@Injectable({ providedIn: 'root' })
export class ToastService {
  private toastSubject = new BehaviorSubject<ToastState | null>(null);
  toast$ = this.toastSubject.asObservable();

  private hideTimer: any = null;

  show(message: string, type: ToastType = 'success', durationMs = 2500) {
    this.toastSubject.next({ message, type });

    if (this.hideTimer) clearTimeout(this.hideTimer);
    this.hideTimer = setTimeout(() => this.clear(), durationMs);
  }

  clear() {
    this.toastSubject.next(null);
    if (this.hideTimer) {
      clearTimeout(this.hideTimer);
      this.hideTimer = null;
    }
  }
}
