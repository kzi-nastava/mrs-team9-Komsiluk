import { Component, EventEmitter, Input, Output, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { ModalShellComponent } from '../../../../../../shared/components/modal-shell/modal-shell.component';

@Component({
  selector: 'app-block-user-confirm-dialog',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, ModalShellComponent],
  templateUrl: './block-user-confirm-dialog.component.html',
  styleUrl: './block-user-confirm-dialog.component.css',
})
export class BlockUserConfirmDialogComponent {
  @Input() open = false;
  @Input() email = '';

  @Output() cancel = new EventEmitter<void>();
  @Output() confirm = new EventEmitter<string>();

  constructor() {}

  reasonCtrl = new FormControl<string>('', {
    nonNullable: true,
    validators: [Validators.required, Validators.minLength(3)],
  });

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['open']?.currentValue === true) {
      this.reasonCtrl.setValue('');
      this.reasonCtrl.markAsPristine();
      this.reasonCtrl.markAsUntouched();
    }
  }

  onConfirmClick() {
    if (this.reasonCtrl.invalid) return;
    this.confirm.emit(this.reasonCtrl.value.trim());
  }
}
