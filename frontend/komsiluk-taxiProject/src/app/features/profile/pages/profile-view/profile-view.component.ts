import { Component } from '@angular/core';
import { ProfileSidebarComponent } from '../../components/profile-sidebar/profile-sidebar.component';
import { ProfileDetailsComponent } from '../../components/profile-details/profile-details.component';
import { UserModeService } from '../../../../shared/util/user_mode/user-mode.service';
import { AuthService, UserRole } from '../../../../features/auth/services/auth';

@Component({
  selector: 'app-profile-view',
  imports: [ProfileSidebarComponent, ProfileDetailsComponent],
  templateUrl: './profile-view.component.html',
  styleUrl: './profile-view.component.css',
})
export class ProfileViewComponent {
  isDriver : boolean = false;
  activeToday = '5h 23m';

  constructor(private mode: UserModeService, private auth: AuthService) {
    this.mode.getMode$().subscribe(m => this.isDriver = (m === 'driver'));
    this.isDriver = this.auth.getRole() === UserRole.DRIVER;
  }
}
