import { Component, EventEmitter, Input, Output, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ModalShellComponent } from '../../../../../shared/components/modal-shell/modal-shell.component';


@Component({
  selector: 'app-cancel-ride-dialog',
  imports: [CommonModule, ModalShellComponent],
  templateUrl: './cancel-ride-dialog.component.html',
  styleUrl: './cancel-ride-dialog.component.css',
})
export class CancelRideDialogComponent {
  @Input() open = false;
  @Input() rideId!: number;

  @Output() cancel = new EventEmitter<void>();
  @Output() confirm = new EventEmitter<{ rideId: number; reason: string }>();

  reason = signal('');

  onConfirm() {
    const value = this.reason().trim();
    if (!value) return; // možeš dodati toast kasnije
    this.confirm.emit({ rideId: this.rideId, reason: value });
  }
}
