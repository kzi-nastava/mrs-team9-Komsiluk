// navbar.component.ts

import { Component, EventEmitter, Output } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css'],
})
export class NavbarComponent {
  @Output() menuClick = new EventEmitter<void>();
  @Output() rightMenuClick = new EventEmitter<void>();
  @Output() filterClick = new EventEmitter<void>();
  @Output() driverHistoryChange = new EventEmitter<boolean>(); // Dodajemo EventEmitter za slanje promene

  isDriverHistory = false;

  constructor(private router: Router) {
    this.router.events.pipe(filter(e => e instanceof NavigationEnd)).subscribe(() => {
      this.isDriverHistory = this.router.url.startsWith('/driver-history');
      this.driverHistoryChange.emit(this.isDriverHistory); // Emituj promenu isDriverHistory
    });
  }

  goHome() {
    this.router.navigateByUrl('/');
  }
}
