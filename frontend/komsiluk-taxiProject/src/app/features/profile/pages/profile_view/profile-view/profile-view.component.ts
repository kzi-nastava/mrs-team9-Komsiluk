import { Component } from '@angular/core';
import { ProfileSidebarComponent } from '../../../components/profile-sidebar/profile-sidebar.component';
import { ProfileDetailsComponent } from '../../../components/profile-details/profile-details.component';


@Component({
  selector: 'app-profile-view',
  imports: [ProfileSidebarComponent, ProfileDetailsComponent],
  templateUrl: './profile-view.component.html',
  styleUrl: './profile-view.component.css',
})
export class ProfileViewComponent {
}
