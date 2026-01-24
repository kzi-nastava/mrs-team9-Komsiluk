import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ModalShellComponent } from '../../../../../shared/components/modal-shell/modal-shell.component';

@Component({
  selector: 'app-driver-start-ride-confirm-dialog',
  standalone: true,
  imports: [CommonModule, ModalShellComponent],
  templateUrl: './driver-start-ride-confirm-dialog.component.html',
  styleUrl: './driver-start-ride-confirm-dialog.component.css',
})
export class DriverStartRideConfirmDialogComponent {

  constructor() {}

  @Input() open = false;

  @Output() cancel = new EventEmitter<void>();
  @Output() confirm = new EventEmitter<void>();
}
