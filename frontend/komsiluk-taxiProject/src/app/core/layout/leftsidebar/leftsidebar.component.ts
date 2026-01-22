import { Component, Input, Signal } from '@angular/core';
import { CommonModule } from '@angular/common';

import { AuthService, UserRole } from '../../auth/services/auth.service';

import { PassengerLeftMenuComponent } from '../components/passenger/passenger-left-menu/passenger-left-menu.component';
import { AdminLeftMenuComponent } from '../components/admin/admin-left-menu/admin-left-menu.component';
import { GuestLeftMenuComponent } from '../components/guest/guest-left-menu/guest-left-menu.component';
import { DriverSidebarComponent } from "../components/driver/driver-left-menu/driver-left-menu.component";

@Component({
  selector: 'app-leftsidebar',
  standalone: true,
  imports: [
    CommonModule,
    PassengerLeftMenuComponent,
    AdminLeftMenuComponent,
    GuestLeftMenuComponent,
    DriverSidebarComponent
],
  templateUrl: './leftsidebar.component.html',
  styleUrl: './leftsidebar.component.css',
})
export class LeftSidebarComponent {
  @Input() open = false;

  userRole: Signal<UserRole>;
  UserRole = UserRole;

  constructor(private auth: AuthService) {
    this.userRole = this.auth.userRole;
  }
}
