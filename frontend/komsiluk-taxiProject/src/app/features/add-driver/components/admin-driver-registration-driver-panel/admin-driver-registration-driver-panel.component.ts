import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-admin-driver-registration-driver-panel',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './admin-driver-registration-driver-panel.component.html',
  styleUrls: ['./admin-driver-registration-driver-panel.component.css'],
})
export class AdminDriverRegistrationDriverPanelComponent {
  @Input({ required: true }) form!: FormGroup;
  @Output() next = new EventEmitter<void>();

  c(name: string) { return this.form.get(name)!; }

  isInvalid(name: string) {
    const ctrl = this.c(name);
    return ctrl.invalid && (ctrl.touched || ctrl.dirty);
  }

  onFileSelected(ev: Event) {
    const input = ev.target as HTMLInputElement;
    const file = input.files?.[0] ?? null;
    this.form.patchValue({ profilePhoto: file });
    this.form.get('profilePhoto')?.markAsTouched();
  }
}
