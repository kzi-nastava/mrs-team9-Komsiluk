import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export type UserMode = 'user' | 'driver';

@Injectable({ providedIn: 'root' })
export class UserModeService {
  private mode$ = new BehaviorSubject<UserMode>('user');

  setMode(mode: UserMode) {
    localStorage.setItem('userMode', mode);
    this.mode$.next(mode);
  }

  getMode$() {
    return this.mode$.asObservable();
  }

  getModeSnapshot(): UserMode {
    return this.mode$.value;
  }

  constructor() {
    const saved = localStorage.getItem('userMode') as UserMode | null;
    if (saved === 'user' || saved === 'driver') this.mode$.next(saved);
  }
}