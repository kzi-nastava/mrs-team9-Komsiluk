import { Component, EventEmitter, Input, Output, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { ModalShellComponent } from '../../../../../../shared/components/modal-shell/modal-shell.component';

@Component({
  selector: 'app-add-favorite-dialog',
  imports: [CommonModule, ReactiveFormsModule, ModalShellComponent],
  templateUrl: './add-favorite-dialog.component.html',
  styleUrl: './add-favorite-dialog.component.css',
})
export class AddFavoriteDialogComponent implements OnChanges {

  @Input() open = false;
  @Input() defaultName = '';

  @Output() cancel = new EventEmitter<void>();
  @Output() confirm = new EventEmitter<string>();

  nameCtrl = new FormControl<string>('', { nonNullable: true, validators: [Validators.required] });

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['open']?.currentValue === true) {
      this.nameCtrl.setValue(this.defaultName ?? '');
      this.nameCtrl.markAsPristine();
      this.nameCtrl.markAsUntouched();
    }
  }

  onConfirmClick() {
    this.nameCtrl.markAsTouched();
    
    if (this.nameCtrl.invalid) return;
    this.confirm.emit(this.nameCtrl.value.trim());
  }
}
