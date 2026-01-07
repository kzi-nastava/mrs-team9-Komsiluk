import { Component, Input } from '@angular/core';
import { RouterLink } from "@angular/router";
import { UserProfileResponseDTO } from '../../../../shared/models/profile.models';

@Component({
  selector: 'app-profile-details',
  imports: [RouterLink],
  templateUrl: './profile-details.component.html',
  styleUrl: './profile-details.component.css',
})
export class ProfileDetailsComponent {
  @Input() isDriver = false;
  @Input() profile: UserProfileResponseDTO | null = null;
}