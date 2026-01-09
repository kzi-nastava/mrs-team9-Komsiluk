import { Component, Input, Output, EventEmitter } from '@angular/core';
import { RouterLink, Router } from '@angular/router';
import { AuthService } from '../../../../core/auth/services/auth.service';
import { UserProfileResponseDTO } from '../../../../shared/models/profile.models';

@Component({
  selector: 'app-profile-sidebar',
  imports: [RouterLink],
  templateUrl: './profile-sidebar.component.html',
  styleUrl: './profile-sidebar.component.css',
})
export class ProfileSidebarComponent {
  @Input() isDriver = false;
  @Input() isPassenger = false;
  @Input() activeToday: string = '-';
  @Input() profile: UserProfileResponseDTO | null = null;
  
  @Input() isBlocked = false;
  @Output() blockedClick = new EventEmitter<void>();

  authService: AuthService;
  router: Router;

  constructor(private auth: AuthService, private rout: Router) {
    this.authService = auth;
    this.router = rout;
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
