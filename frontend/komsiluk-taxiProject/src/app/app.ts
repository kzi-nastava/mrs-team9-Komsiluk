import { Component, signal, OnInit } from '@angular/core';
import { RouterOutlet, Router, NavigationStart } from '@angular/router';
import { CommonModule } from '@angular/common';

import { NavbarComponent } from './core/layout/navbar/navbar.component';
import { ToastComponent } from './shared/components/toast/toast/toast.component';
import { LeftSidebarComponent } from './core/layout/leftsidebar/leftsidebar.component';
import { RightsidebarComponent } from './core/layout/rightsidebar.component/rightsidebar.component';
import { RideHistoryFilterPanelComponent } from './features/driver-history/components/ride-history-filter-panel/ride-history-filter-panel';

import { RideHistoryFilterService } from './features/driver-history/services/driver-history-filter.service';
import { filter } from 'rxjs';

import { ConfirmBookingDialogComponent } from './core/layout/components/passenger/book_ride/confirm-booking-dialog/confirm-booking-dialog.component';
import { ConfirmBookingModalService } from './shared/components/modal-shell/services/confirm-booking-modal.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    RouterOutlet,
    NavbarComponent,
    LeftSidebarComponent,
    RightsidebarComponent,
    ToastComponent,
    RideHistoryFilterPanelComponent,
    ConfirmBookingDialogComponent
  ],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  protected readonly title = signal('komsiluk-taxiProject');

  isLeftSidebarOpen = false;
  rightOpen = false;

  constructor(public filterSvc: RideHistoryFilterService, private router: Router, public confirmModal: ConfirmBookingModalService) {}

  ngOnInit(): void {
  this.router.events
    .pipe(filter(e => e instanceof NavigationStart))
    .subscribe(() => {
      this.isLeftSidebarOpen = false;
      this.rightOpen = false;
    });
  }

  toggleLeftSidebar() { this.isLeftSidebarOpen = !this.isLeftSidebarOpen; }
  toggleRightSidebar() { this.rightOpen = !this.rightOpen; if (this.rightOpen) this.isLeftSidebarOpen = false; }

  toggleFilter() { this.filterSvc.toggle(); }

  onFilterApplied(range: { from: string; to: string }) {
    this.filterSvc.apply(range);
    this.filterSvc.close();
  }

  onFilterReset() {
    this.filterSvc.reset();
    this.filterSvc.close();
  }

  closeRightSidebar(): void {
  this.rightOpen = false;
  }
}
