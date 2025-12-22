import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import {  RouterLink } from '@angular/router';
import { AuthService, UserRole } from '../../../features/auth/services/auth';
import { Signal } from '@angular/core';

@Component({
  selector: 'app-rightsidebar',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './rightsidebar.component.html',
  styleUrls: ['./rightsidebar.component.css'],
})
export class RightsidebarComponent {
  @Input() open = false;
  userRole: Signal<UserRole>;
  UserRole = UserRole;


  constructor(private auth: AuthService) {
    this.userRole = this.auth.role.asReadonly();
  }
}
