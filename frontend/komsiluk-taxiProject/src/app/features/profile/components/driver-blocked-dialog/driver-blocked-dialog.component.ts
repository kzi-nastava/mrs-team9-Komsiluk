import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ModalShellComponent } from '../../../../shared/components/modal-shell/modal-shell.component';

@Component({
  selector: 'app-driver-blocked-dialog',
  standalone: true,
  imports: [CommonModule, ModalShellComponent],
  templateUrl: './driver-blocked-dialog.component.html',
  styleUrl: './driver-blocked-dialog.component.css',
})
export class DriverBlockedDialogComponent {
  @Input() open = false;
  @Input() reason = '';
  @Input() adminEmail = '';

  @Output() close = new EventEmitter<void>();
}
