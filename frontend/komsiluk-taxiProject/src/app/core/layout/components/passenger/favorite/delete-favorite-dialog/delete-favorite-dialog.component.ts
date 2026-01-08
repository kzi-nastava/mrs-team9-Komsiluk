import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ModalShellComponent } from '../../../../../../shared/components/modal-shell/modal-shell.component';

@Component({
  selector: 'app-delete-favorite-dialog',
  standalone: true,
  imports: [CommonModule, ModalShellComponent],
  templateUrl: './delete-favorite-dialog.component.html',
  styleUrl: './delete-favorite-dialog.component.css',
})
export class DeleteFavoriteDialogComponent {
  @Input() open = false;
  @Input() title = '';

  @Output() cancel = new EventEmitter<void>();
  @Output() confirm = new EventEmitter<void>();
}
