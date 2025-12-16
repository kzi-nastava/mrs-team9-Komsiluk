import { Component } from '@angular/core';
import { ProfileSidebarComponent } from '../../components/profile-sidebar/profile-sidebar.component';
import { ProfileDetailsComponent } from '../../components/profile-details/profile-details.component';

@Component({
  selector: 'app-user-profile-page',
  standalone: true,
  imports: [ProfileSidebarComponent, ProfileDetailsComponent],
  templateUrl: './user-profile-page.component.html',
  styleUrl: './user-profile-page.component.css',
})
export class UserProfilePageComponent {

}
