import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ModalShellComponent } from '../../../../../shared/components/modal-shell/modal-shell.component';
import { DriverActivityConfirmMode } from '../../../../../shared/components/modal-shell/services/driver-activity-confirm-modal.service';

@Component({
  selector: 'app-driver-activity-confirm-dialog',
  standalone: true,
  imports: [CommonModule, ModalShellComponent],
  templateUrl: './driver-activity-confirm-dialog.component.html',
  styleUrls: ['./driver-activity-confirm-dialog.component.css'],
})
export class DriverActivityConfirmDialogComponent {
  @Input() open = false;
  @Input() mode: DriverActivityConfirmMode = 'TO_INACTIVE';

  @Output() cancel = new EventEmitter<void>();
  @Output() confirm = new EventEmitter<void>();

  get title() {
    return this.mode === 'TO_INACTIVE' ? 'Go Inactive' : 'Go Active';
  }

  get text() {
    return this.mode === 'TO_INACTIVE'
      ? `While inactive, you won’t receive any new ride requests. Are you sure you want to go inactive?`
      : `When you go active, you will start receiving new ride requests. Are you ready to go active now?`;
  }
}
