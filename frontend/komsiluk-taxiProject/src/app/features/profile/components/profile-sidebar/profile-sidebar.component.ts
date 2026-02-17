import { Component, Input, Output, EventEmitter } from '@angular/core';
import { RouterLink, Router } from '@angular/router';
import { AuthService } from '../../../../core/auth/services/auth.service';
import { UserProfileResponseDTO } from '../../../../shared/models/profile.models';
import { RideService } from '../../../../core/layout/components/passenger/book_ride/services/ride.service';
import { finalize } from 'rxjs/internal/operators/finalize';
import { ToastService } from '../../../../shared/components/toast/toast.service';

@Component({
  selector: 'app-profile-sidebar',
  imports: [RouterLink],
  templateUrl: './profile-sidebar.component.html',
  styleUrls: ['./profile-sidebar.component.css'],
})
export class ProfileSidebarComponent {
  @Input() isDriver = false;
  @Input() isPassenger = false;
  @Input() activeToday: string = '-';
  @Input() profile: UserProfileResponseDTO | null = null;
  
  @Input() isBlocked = false;
  @Output() blockedClick = new EventEmitter<void>();

  @Input() avatarVersion = 0;
  @Output() profileImagePicked = new EventEmitter<File>();

  authService: AuthService;
  router: Router;

  hasActiveRide = false;

  constructor(private auth: AuthService, private rout: Router, private rideService: RideService, private toast: ToastService) {
    this.authService = auth;
    this.router = rout;
  }

  private readonly IMG_BASE = 'http://localhost:8081';
  get avatarSrc(): string | null {
    const url = this.profile?.profileImageUrl?.trim();
    if (!url) return null;
    return `${this.IMG_BASE}${url}?v=${this.avatarVersion}`;
  }

  ngOnInit() {
    if (this.isDriver) {
      this.rideService.getDriverCurrentRide(this.authService.userId()!).subscribe((resp: any) => {
        if (resp.status === 200) {
          this.hasActiveRide = true;
        }
      });
    }
  }

  onPickFile(ev: Event) {
    const input = ev.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;

    input.value = '';

    this.profileImagePicked.emit(file);
  }

  logout() {
    if (!this.isDriver) {
      this.router.navigate(['/message', 'confirm-logout']);
      return;
    }

    const driverId = this.auth.userId();
    if (!driverId) return;

    this.rideService.getDriverCurrentRide(driverId).subscribe({
      next: (resp: any) => {
        if (resp && resp.status === 200) {
          this.toast.show('You have an active or assigned ride. Please finish it before logging out.');
        } else {
          this.router.navigate(['/message', 'confirm-logout']);
        }
      },
      error: () => {
        this.router.navigate(['/message', 'confirm-logout']);
      }
    });
  }

  goToFavorites() {
    this.router.navigate(['/'], {
      queryParams: {
        lp: '1',
        section: 'fav',
        scroll: 'fav'
      }
    });
  }

  goToScheduled() {
    this.router.navigate(['/'], {
      queryParams: {
        lp: '1',
        section: 'sched',
        scroll: 'sched'
      }
    });
  }

  goToRideHistory() {
    if (this.isDriver) {
      this.router.navigate(['/driver-history']);
    } else if (this.isPassenger) {
      this.router.navigate(['/passenger-history']);
    } else {
      this.router.navigate(['/admin/ride-history']);
    }
  }
}
