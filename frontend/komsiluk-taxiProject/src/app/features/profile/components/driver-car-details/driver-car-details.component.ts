import { Component, Input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { UserProfileResponseDTO } from '../../../../shared/models/profile.models';

@Component({
  selector: 'app-driver-car-details',
  imports: [RouterLink],
  templateUrl: './driver-car-details.component.html',
  styleUrl: './driver-car-details.component.css',
})
export class DriverCarDetailsComponent {
  @Input() profile: UserProfileResponseDTO | null = null;
}
