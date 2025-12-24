import { Component, Input } from '@angular/core';
import { RouterLink, Router } from '@angular/router';
import { AuthService } from '../../../../features/auth/services/auth';

@Component({
  selector: 'app-profile-sidebar',
  imports: [RouterLink],
  templateUrl: './profile-sidebar.component.html',
  styleUrl: './profile-sidebar.component.css',
})
export class ProfileSidebarComponent {
  @Input() isDriver = false;
  @Input() activeToday: string = '5h 23m';
  authService: AuthService;
  router: Router;

  constructor(private auth: AuthService, private rout: Router) {
    this.authService = auth;
    this.router = rout;
  }
  logout() {
    this.router.navigate(['/message', 'confirm-logout']);
  }
}
