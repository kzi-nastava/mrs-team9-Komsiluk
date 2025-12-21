import { Injectable, signal } from '@angular/core';

export type RideHistoryRange = { from: string; to: string }; // "YYYY-MM-DD" iz <input type="date">

@Injectable({ providedIn: 'root' })
export class RideHistoryFilterService {
  readonly open = signal(false);
  readonly range = signal<RideHistoryRange>({ from: '', to: '' });

  toggle() { this.open.update(v => !v); }
  close() { this.open.set(false); }
  openPanel() { this.open.set(true); }

  apply(range: RideHistoryRange) { this.range.set(range); }
  reset() { this.range.set({ from: '', to: '' }); }
}
