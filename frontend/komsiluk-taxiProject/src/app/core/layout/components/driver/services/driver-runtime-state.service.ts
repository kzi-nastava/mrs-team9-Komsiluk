import { Injectable, computed, signal } from '@angular/core';
import { interval, Subscription } from 'rxjs';
import { ProfileService } from '../../../../../features/profile/services/profile.service';
import { AuthService, UserRole } from '../../../../auth/services/auth.service';
import { DriverService, DriverStatus } from './driver.service';
import { ToastService } from '../../../../../shared/components/toast/toast.service';

@Injectable({ providedIn: 'root' })
export class DriverRuntimeStateService {
  private tickSub: Subscription | null = null;

  private statusSig = signal<DriverStatus>('INACTIVE');
  status = computed(() => this.statusSig());

  private minutesSig = signal<number>(0);
  activeMinutes = computed(() => this.minutesSig());

  activeTodayText = computed(() => {
    const minutes = this.minutesSig();
    const h = Math.floor(minutes / 60);
    const m = minutes % 60;
    return `${h}h ${m}m`;
  });

  isActiveLike = computed(() => {
    const s = this.statusSig();
    return s === 'ACTIVE' || s === 'IN_RIDE';
  });

  constructor(
    private profileSvc: ProfileService,
    private auth: AuthService,
    private driverSvc: DriverService,
    private toast: ToastService
  ) {}

  setStatus(s: DriverStatus) {
    this.statusSig.set(s);
    this.ensureTimer();
  }

  initIfDriver() {
    if (this.auth.userRole() !== UserRole.DRIVER) return;
    this.refreshFromBackend();
  }

  refreshFromBackend() {
    this.profileSvc.getMyProfile().subscribe({
      next: (p) => {
        this.minutesSig.set(p.activeMinutesLast24h ?? 0);
        const s = (p.driverStatus ?? 'INACTIVE') as DriverStatus;
        this.setStatus(s);
      },
      error: () => {}
    });
  }

  toggleStatusWithBackend() {
    const id = Number(this.auth.userId());
    if (!id) return;

    const current = this.statusSig();
    const next: DriverStatus = (current === 'INACTIVE') ? 'ACTIVE' : 'INACTIVE';

    this.driverSvc.updateStatus(id, next).subscribe({
      next: () => {
        this.refreshFromBackend();
      },
      error: (err) => {
        this.toast.show(err?.error?.message || 'Status update failed.');
      }
    });
  }

  private ensureTimer() {
    const shouldRun = this.isActiveLike();
    if (!shouldRun) {
      this.stopTimer();
      return;
    }
    if (this.tickSub) return;

    this.tickSub = interval(60_000).subscribe(() => {
      if (!this.isActiveLike()) return;
      this.minutesSig.set(this.minutesSig() + 1);
    });
  }

  private stopTimer() {
    this.tickSub?.unsubscribe();
    this.tickSub = null;
  }
}
