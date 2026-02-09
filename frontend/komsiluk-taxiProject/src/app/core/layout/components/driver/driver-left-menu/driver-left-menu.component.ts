import { Component, signal, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DriverActivityConfirmModalService } from '../../../../../shared/components/modal-shell/services/driver-activity-confirm-modal.service';
import { DriverRuntimeStateService } from '../services/driver-runtime-state.service';
import { DriverCurrentRidePanelComponent } from '../driver-current-ride-panel/driver-current-ride-panel.component';
import { DriverScheduledRidesPanelComponent } from '../driver-scheduled-rides-panel/driver-scheduled-rides-panel.component';
import { AfterViewInit, OnDestroy, inject } from '@angular/core';
import { Subscription } from 'rxjs';
import { LeftSidebarCommandService } from '../../passenger/services/left-sidebar-command-service.service';


@Component({
  selector: 'app-driver-left-menu',
  standalone: true,
  imports: [CommonModule, DriverCurrentRidePanelComponent,DriverScheduledRidesPanelComponent],
  templateUrl: './driver-left-menu.component.html',
  styleUrl: './driver-left-menu.component.css',
})
export class DriverLeftMenuComponent implements AfterViewInit, OnDestroy {
  currentOpen = signal(true);
  scheduledOpen = signal(false);

  private leftCmd = inject(LeftSidebarCommandService);
  private cmdSub?: Subscription;

  constructor(
    public modal: DriverActivityConfirmModalService,
    public driverState: DriverRuntimeStateService
  ) {}

  toggle(which: 'current' | 'sched') {
    if (which === 'current') {
      this.currentOpen.set(!this.currentOpen());
    }
    if (which === 'sched') {
      this.scheduledOpen.set(!this.scheduledOpen());
    }
  }

  ngAfterViewInit(): void {
    this.cmdSub = this.leftCmd.cmd$.subscribe(cmd => {
      if (cmd.section === 'sched') {
        this.openSection('sched');
      }
    });
  }

  private openSection(section: 'current' | 'sched') {
    this.currentOpen.set(section === 'current');
    this.scheduledOpen.set(section === 'sched');
  }

  onSwitchClick() {
    const toActive = !this.driverState.isActiveLike();
    this.modal.openModal(toActive ? 'TO_ACTIVE' : 'TO_INACTIVE');
  }

  ngOnDestroy(): void {
    this.cmdSub?.unsubscribe();
  }
}
