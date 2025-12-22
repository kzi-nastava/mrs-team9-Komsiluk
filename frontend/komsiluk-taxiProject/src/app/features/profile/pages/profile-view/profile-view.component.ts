import { Component } from '@angular/core';
import { ProfileSidebarComponent } from '../../components/profile-sidebar/profile-sidebar.component';
import { ProfileDetailsComponent } from '../../components/profile-details/profile-details.component';
import { UserModeService } from '../../../../shared/util/user_mode/user-mode.service';

@Component({
  selector: 'app-profile-view',
  imports: [ProfileSidebarComponent, ProfileDetailsComponent],
  templateUrl: './profile-view.component.html',
  styleUrl: './profile-view.component.css',
})
export class ProfileViewComponent {
  isDriver = false;
  activeToday = '5h 23m';

  constructor(private mode: UserModeService) {
    this.mode.getMode$().subscribe(m => this.isDriver = (m === 'driver'));
    
  }
}
