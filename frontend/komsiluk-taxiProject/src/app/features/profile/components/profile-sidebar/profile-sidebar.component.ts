import { Component, Input, Output, EventEmitter } from '@angular/core';
import { RouterLink, Router } from '@angular/router';
import { AuthService } from '../../../../core/auth/services/auth.service';
import { UserProfileResponseDTO } from '../../../../shared/models/profile.models';

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

  constructor(private auth: AuthService, private rout: Router) {
    this.authService = auth;
    this.router = rout;
  }

  private readonly IMG_BASE = 'http://localhost:8081';
  get avatarSrc(): string | null {
    const url = this.profile?.profileImageUrl?.trim();
    if (!url) return null;
    return `${this.IMG_BASE}${url}?v=${this.avatarVersion}`;
  }

  onPickFile(ev: Event) {
    const input = ev.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;

    input.value = '';

    this.profileImagePicked.emit(file);
  }

  logout() {
    this.router.navigate(['/message', 'confirm-logout']);
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
}
