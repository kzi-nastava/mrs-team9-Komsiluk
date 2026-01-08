import { Component, EventEmitter, Input, Output, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { ModalShellComponent } from '../../../../../../shared/components/modal-shell/modal-shell.component';

@Component({
  selector: 'app-rename-favorite-dialog',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, ModalShellComponent],
  templateUrl: './rename-favorite-dialog.component.html',
  styleUrl: './rename-favorite-dialog.component.css',
})
export class RenameFavoriteDialogComponent implements OnChanges {
  @Input() open = false;
  @Input() currentTitle = '';

  @Output() cancel = new EventEmitter<void>();
  @Output() confirm = new EventEmitter<string>();

  nameCtrl = new FormControl<string>('', { nonNullable: true, validators: [Validators.required] });

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['open']?.currentValue === true) {
      this.nameCtrl.setValue(this.currentTitle ?? '');
      this.nameCtrl.markAsPristine();
      this.nameCtrl.markAsUntouched();
    }
  }

  onConfirmClick() {
    const v = this.nameCtrl.value.trim();
    if (!v) return;
    this.confirm.emit(v);
  }
}
