import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { ProfileSidebarComponent } from '../../components/profile-sidebar/profile-sidebar.component';
import { DriverCarDetailsComponent } from '../../components/driver-car-details/driver-car-details.component';
import { UserModeService } from '../../../../shared/util/user_mode/user-mode.service';

@Component({
  selector: 'app-driver-car-view',
  imports: [ProfileSidebarComponent, DriverCarDetailsComponent],
  templateUrl: './driver-car-view.component.html',
  styleUrl: './driver-car-view.component.css',
})
export class DriverCarViewComponent {
  isDriver = false;
  activeToday = '5h 23m';

  constructor(private mode: UserModeService, private router: Router) {
    this.isDriver = this.mode.getModeSnapshot() === 'driver';

    if (!this.isDriver) {
      this.router.navigate(['/profile']);
    }
  }
}
