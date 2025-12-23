// navbar.component.ts

import { Component, EventEmitter, Output } from '@angular/core';
import { Router, NavigationEnd, RouterLink } from '@angular/router';
import { filter } from 'rxjs/operators';
import { CommonModule } from '@angular/common';
import { AuthService, UserRole } from '../../../features/auth/services/auth';
import { Signal } from '@angular/core';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css'],
})
export class NavbarComponent {
  @Output() menuClick = new EventEmitter<void>();
  @Output() rightMenuClick = new EventEmitter<void>();
  @Output() filterClick = new EventEmitter<void>();
  @Output() driverHistoryChange = new EventEmitter<boolean>(); // Dodajemo EventEmitter za slanje promene

  isHome = false;
  isDriverHistory = false;
  userRole: Signal<UserRole>;
  UserRole = UserRole;

  constructor(private router: Router, private authService: AuthService) {
    this.router.events.pipe(filter(e => e instanceof NavigationEnd)).subscribe(() => {
      this.isDriverHistory = this.router.url.startsWith('/driver-history');
      this.driverHistoryChange.emit(this.isDriverHistory); // Emituj promenu isDriverHistory
    });
    this.router.events
      .pipe(filter(e => e instanceof NavigationEnd))
      .subscribe(() => {
        const url = this.router.url;
        this.isHome = url === '/' || url === '';
      });
    this.userRole = this.authService.role.asReadonly();
  }

  goHome() {
    this.router.navigateByUrl('/');
  }
}
