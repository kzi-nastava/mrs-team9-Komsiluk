import { Component, Input, Signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService, UserRole } from '../../../core/auth/services/auth.service';
import { PassengerLeftMenuComponent } from '../components/passenger/passenger-left-menu/passenger-left-menu.component';
import { AdminLeftMenuComponent } from '../components/admin/admin-left-menu/admin-left-menu.component';

@Component({
  selector: 'app-leftsidebar',
  standalone: true,
  imports: [CommonModule, PassengerLeftMenuComponent, AdminLeftMenuComponent],
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
