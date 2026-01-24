import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-admin-driver-registration-vehicle-panel',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './admin-driver-registration-vehicle-panel.component.html',
  styleUrls: ['./admin-driver-registration-vehicle-panel.component.css'],
})
export class AdminDriverRegistrationVehiclePanelComponent {
  @Input({ required: true }) form!: FormGroup;
  @Input() creating = false;

  @Output() back = new EventEmitter<void>();
  @Output() create = new EventEmitter<void>();

  c(name: string) { return this.form.get(name)!; }

  isInvalid(name: string) {
    const ctrl = this.c(name);
    return ctrl.invalid && (ctrl.touched || ctrl.dirty);
  }
}
