import { Component } from '@angular/core';
import { ProfileSidebarComponent } from '../../components/profile-sidebar/profile-sidebar.component';
import { DriverCarDetailsComponent } from '../../components/driver-car-details/driver-car-details.component';
import { ProfileService } from '../../services/profile.service';
import { UserProfileResponseDTO } from '../../../../shared/models/profile.models';
import { AuthService, UserRole } from '../../../../core/auth/services/auth.service';
import { ChangeDetectorRef } from '@angular/core';

@Component({
  selector: 'app-driver-car-view',
  imports: [ProfileSidebarComponent, DriverCarDetailsComponent],
  templateUrl: './driver-car-view.component.html',
  styleUrl: './driver-car-view.component.css',
})
export class DriverCarViewComponent {
  profile: UserProfileResponseDTO | null = null;
  loading = false;

  constructor(private profileService: ProfileService, private auth: AuthService, private cdr: ChangeDetectorRef) {}

  get isDriver(): boolean {
    return this.auth.userRole() === UserRole.DRIVER;
  }

  get activeToday(): string {
    if (!this.profile) {
      return '-';
    }
    const minutes = this.profile.activeMinutesLast24h;
    const hours = Math.floor(minutes / 60);
    const mins = minutes % 60;
    return `${hours}h ${mins}m`;
  }

  ngOnInit(): void {
    this.loading = true;
    this.profileService.getMyProfile().subscribe({
      next: (p) => { this.profile = p; this.loading = false; this.cdr.detectChanges(); },
      error: () => { this.loading = false; this.cdr.detectChanges(); },
    });
  }
}
