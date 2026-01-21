import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators, FormGroup } from '@angular/forms';

import { AuthCardComponent } from '../../auth/components/auth-card/auth-card.component';

import { AdminDriverRegistrationDriverPanelComponent } from '../components/admin-driver-registration-driver-panel/admin-driver-registration-driver-panel.component';
import { AdminDriverRegistrationVehiclePanelComponent } from '../components/admin-driver-registration-vehicle-panel/admin-driver-registration-vehicle-panel.component';
import { AdminDriverRegistrationSuccessPanelComponent } from '../components/admin-driver-registration-success-panel/admin-driver-registration-success-panel.component';

import { DriverService } from '../services/driver.service';
import { ToastService } from '../../../shared/components/toast/toast.service';
import { DriverCreateDTO } from '../../../shared/models/driver.models';

type Step = 1 | 2 | 3;

@Component({
  selector: 'app-admin-driver-registration-page',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    AuthCardComponent,
    AdminDriverRegistrationDriverPanelComponent,
    AdminDriverRegistrationVehiclePanelComponent,
    AdminDriverRegistrationSuccessPanelComponent,
  ],
  templateUrl: './admin-driver-registration-page.component.html',
  styleUrls: ['./admin-driver-registration-page.component.css'],
})
export class AdminDriverRegistrationPageComponent {
  step = signal<Step>(1);
  creating = signal(false);

  driverForm: FormGroup;
  vehicleForm: FormGroup;

  private selectedProfileImage: File | null = null;

  constructor(
    private fb: FormBuilder,
    private driverService: DriverService,
    private toast: ToastService
  ) {
    this.driverForm = this.fb.group({
      firstName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(50), Validators.pattern(/^[^\d]+$/)]],
      lastName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(50), Validators.pattern(/^[^\d]+$/)]],
      address: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(100)]],
      city: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
      phoneNumber: ['', [Validators.required, Validators.pattern(/^\+?\d{7,15}$/)]],
      email: ['', [Validators.required, Validators.email]],
      profilePhoto: [null],
    });

    this.vehicleForm = this.fb.group({
      model: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(60)]],
      type: ['STANDARD', [Validators.required]],
      licencePlate: ['', [Validators.required, Validators.pattern(/^[A-ZČĆŠĐŽ]{1,3}-\d{2,4}-[A-ZČĆŠĐŽ]{1,3}$/)]],
      seatCount: ['', [Validators.required, Validators.pattern(/^\d+$/), Validators.min(1), Validators.max(8)]],
      petFriendly: [false],
      babyFriendly: [false],
    });

    this.driverForm.get('profilePhoto')?.valueChanges.subscribe((v: File | null) => {
      this.selectedProfileImage = v instanceof File ? v : null;
    });
  }

  goNextFromDriver() {
    this.driverForm.markAllAsTouched();
    if (this.driverForm.invalid) return;
    this.step.set(2);
  }

  goBackToDriver() {
    this.step.set(1);
  }

  createDriver() {
    this.vehicleForm.markAllAsTouched();
    if (this.vehicleForm.invalid) return;

    const dto: DriverCreateDTO = {
      firstName: String(this.driverForm.value.firstName ?? ''),
      lastName: String(this.driverForm.value.lastName ?? ''),
      address: String(this.driverForm.value.address ?? ''),
      city: String(this.driverForm.value.city ?? ''),
      phoneNumber: String(this.driverForm.value.phoneNumber ?? ''),
      email: String(this.driverForm.value.email ?? ''),
      profileImageUrl: null,
      vehicle: {
        model: String(this.vehicleForm.value.model ?? ''),
        type: this.vehicleForm.value.type,
        licencePlate: String(this.vehicleForm.value.licencePlate ?? ''),
        seatCount: Number(this.vehicleForm.value.seatCount ?? 0),
        babyFriendly: !!this.vehicleForm.value.babyFriendly,
        petFriendly: !!this.vehicleForm.value.petFriendly,
      },
    };

    this.creating.set(true);

    this.driverService.registerDriver(dto, this.selectedProfileImage).subscribe({
      next: () => {
        this.creating.set(false);
        this.step.set(3);
      },
      error: (err) => {
        this.creating.set(false);
        const msg =
          err?.error?.message ||
          (typeof err?.error === 'string' ? err.error : null) ||
          'Driver registration failed.';
        this.toast.show(msg);
      },
    });
  }

  done() {
    this.driverForm.reset({
      firstName: '',
      lastName: '',
      address: '',
      city: '',
      phoneNumber: '',
      email: '',
      profilePhoto: null,
    });
    this.vehicleForm.reset({
      model: '',
      type: 'STANDARD',
      licencePlate: '',
      seatCount: '',
      petFriendly: false,
      babyFriendly: false,
    });
    this.selectedProfileImage = null;
    this.step.set(1);
  }
}
