import { Component, EventEmitter, Output } from '@angular/core';
import { Router, NavigationEnd, RouterLink } from '@angular/router';
import { filter } from 'rxjs/operators';
import { CommonModule } from '@angular/common';
import { AuthService, UserRole } from '../../../core/auth/services/auth.service';
import { Signal } from '@angular/core';
import { DriverRuntimeStateService } from '../components/driver/services/driver-runtime-state.service';

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
  @Output() driverHistoryChange = new EventEmitter<boolean>();

  isHome = false;
  isDriverHistory = false;
  userRole: Signal<UserRole>;
  UserRole = UserRole;

  constructor(private router: Router, private authService: AuthService, public driverState: DriverRuntimeStateService) {
    this.router.events.pipe(filter(e => e instanceof NavigationEnd)).subscribe(() => {
      this.isDriverHistory = this.router.url.startsWith('/driver-history');
      this.driverHistoryChange.emit(this.isDriverHistory);
    });
    this.router.events
      .pipe(filter(e => e instanceof NavigationEnd))
      .subscribe(() => {
        const path = this.router.url.split('?')[0].split('#')[0];
        this.isHome = path === '/' || path === '';
      });
    this.userRole = this.authService.userRole;
  }

  goHome() {
    this.router.navigateByUrl('/');
  }

  @Output() adminMenuClick = new EventEmitter<void>();

}
