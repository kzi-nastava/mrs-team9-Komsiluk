import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';

import { NavbarComponent } from './core/layout/navbar/navbar.component';
import { ToastComponent } from './shared/components/toast/toast/toast.component';
import { LeftSidebarComponent } from './core/layout/leftsidebar/leftsidebar.component';
import { RightsidebarComponent } from './core/layout/rightsidebar.component/rightsidebar.component';
import { RideHistoryFilterPanelComponent } from './features/driver-history/components/ride-history-filter-panel/ride-history-filter-panel';

import { RideHistoryFilterService } from './features/driver-history/services/driver-history-filter.service';

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
    RideHistoryFilterPanelComponent
  ],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('komsiluk-taxiProject');

  isLeftSidebarOpen = false;
  rightOpen = false;

  constructor(public filterSvc: RideHistoryFilterService) {}

  toggleLeftSidebar() { this.isLeftSidebarOpen = !this.isLeftSidebarOpen; }
  toggleRightSidebar() { this.rightOpen = !this.rightOpen; if (this.rightOpen) this.isLeftSidebarOpen = false; }

  toggleFilter() { this.filterSvc.toggle(); }

  onFilterApplied(range: { from: string; to: string }) {
    this.filterSvc.apply(range);
    this.filterSvc.close(); // <--- KLJUČNO: zatvori panel posle Apply
  }

  onFilterReset() {
    this.filterSvc.reset();
    this.filterSvc.close(); // <--- zatvori i posle Reset (može i bez close ako želiš)
  }
}
