import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ModalShellComponent } from '../../../../../shared/components/modal-shell/modal-shell.component';

@Component({
  selector: 'app-stop-ride-dialog',
  imports:  [CommonModule, ModalShellComponent],
  templateUrl: './stop-ride-dialog.html',
  styleUrl: './stop-ride-dialog.css',
})
export class StopRideDialog {
@Input() open = false;
  @Output() cancel = new EventEmitter<void>();
  @Output() confirm = new EventEmitter<void>();
}
