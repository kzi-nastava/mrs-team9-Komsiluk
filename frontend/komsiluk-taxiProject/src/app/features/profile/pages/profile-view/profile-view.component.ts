import { Component } from '@angular/core';
import { ProfileSidebarComponent } from '../../components/profile-sidebar/profile-sidebar.component';
import { ProfileDetailsComponent } from '../../components/profile-details/profile-details.component';
import { ProfileService } from '../../services/profile.service';
import { UserProfileResponseDTO } from '../../../../shared/models/profile.models';
import { AuthService, UserRole } from '../../../../core/auth/services/auth.service';
import { ChangeDetectorRef } from '@angular/core';

@Component({
  selector: 'app-profile-view',
  imports: [ProfileSidebarComponent, ProfileDetailsComponent],
  templateUrl: './profile-view.component.html',
  styleUrl: './profile-view.component.css',
})
export class ProfileViewComponent {
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
