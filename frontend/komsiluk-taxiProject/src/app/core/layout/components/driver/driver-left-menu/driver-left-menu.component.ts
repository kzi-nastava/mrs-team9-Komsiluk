import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DriverActivityConfirmModalService } from '../../../../../shared/components/modal-shell/services/driver-activity-confirm-modal.service';
import { DriverRuntimeStateService } from '../services/driver-runtime-state.service';
import { DriverCurrentRidePanelComponent } from '../driver-current-ride-panel/driver-current-ride-panel.component';

@Component({
  selector: 'app-driver-left-menu',
  standalone: true,
  imports: [CommonModule, DriverCurrentRidePanelComponent],
  templateUrl: './driver-left-menu.component.html',
  styleUrl: './driver-left-menu.component.css',
})
export class DriverLeftMenuComponent {
  currentOpen = signal(true);
  scheduledOpen = signal(false);

  constructor(
    public modal: DriverActivityConfirmModalService,
    public driverState: DriverRuntimeStateService
  ) {}

  toggle(which: 'current' | 'scheduled') {
    if (which === 'current') this.currentOpen.set(!this.currentOpen());
    if (which === 'scheduled') this.scheduledOpen.set(!this.scheduledOpen());
  }

  onSwitchClick() {
    const toActive = !this.driverState.isActiveLike();
    this.modal.openModal(toActive ? 'TO_ACTIVE' : 'TO_INACTIVE');
  }
}
