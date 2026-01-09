import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ModalShellComponent } from '../../../../../../shared/components/modal-shell/modal-shell.component';
import { AccountBlockedModalData } from '../../../../../../shared/components/modal-shell/services/account-blocked-modal.service';

@Component({
  selector: 'app-account-blocked-dialog',
  standalone: true,
  imports: [CommonModule, ModalShellComponent],
  templateUrl: './account-blocked-dialog.component.html',
  styleUrl: './account-blocked-dialog.component.css',
})
export class AccountBlockedDialogComponent {
  @Input() open = false;
  @Input() data: AccountBlockedModalData | null = null;

  @Output() closed = new EventEmitter<void>();
}
