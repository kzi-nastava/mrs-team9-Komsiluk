import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ModalShellComponent } from '../../../shared/components/modal-shell/modal-shell.component';

@Component({
  selector: 'app-panic-dialog',
  standalone: true,
  imports: [CommonModule, ModalShellComponent],
  templateUrl: './panic-dialog.component.html',
  styleUrl: './panic-dialog.component.css'
})
export class PanicDialogComponent {
  @Input() open = false;
  @Output() cancel = new EventEmitter<void>();
  @Output() confirm = new EventEmitter<void>();
}