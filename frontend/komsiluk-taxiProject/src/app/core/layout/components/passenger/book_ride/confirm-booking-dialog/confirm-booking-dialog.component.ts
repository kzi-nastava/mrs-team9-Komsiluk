import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ModalShellComponent } from '../../../../../../shared/components/modal-shell/modal-shell.component';

@Component({
  selector: 'app-confirm-booking-dialog',
  imports: [CommonModule, ModalShellComponent],
  templateUrl: './confirm-booking-dialog.component.html',
  styleUrl: './confirm-booking-dialog.component.css',
})
export class ConfirmBookingDialogComponent {
    @Input() open = false;
    
    @Input() km = 0;
    @Input() minutes = 0;
    @Input() price = 0;

    @Output() cancel = new EventEmitter<void>();
    @Output() confirm = new EventEmitter<void>();
}
