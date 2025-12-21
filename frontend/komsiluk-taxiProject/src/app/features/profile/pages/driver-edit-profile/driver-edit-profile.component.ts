import { Component } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { trimRequired, personName, phoneNumber, licensePlate, seatsCount } from '../../../../shared/util/validators/field-validators.service';

@Component({
  selector: 'app-driver-edit-profile',
  imports: [ReactiveFormsModule],
  templateUrl: './driver-edit-profile.component.html',
  styleUrl: './driver-edit-profile.component.css',
})
export class DriverEditProfileComponent {

  submitted = false;

  form: ReturnType<FormBuilder['group']>;

  constructor(private router: Router, private fb: FormBuilder) {
    this.form = this.fb.group({
    // Profile
    firstName: ['', [trimRequired, Validators.minLength(2), personName]],
    lastName: ['', [trimRequired, Validators.minLength(2), personName]],
    address: ['', [trimRequired, Validators.minLength(5)]],
    city: ['', [trimRequired, Validators.minLength(2)]],
    phone: ['', [trimRequired, phoneNumber]],

    // Car
    model: ['', [trimRequired, Validators.minLength(2)]],
    type: ['', [trimRequired]],
    licensePlate: ['', [trimRequired, licensePlate]],
    seats: ['', [trimRequired, seatsCount]],

    petFriendly: [false],
    childSeatAvailable: [false],
  });
  }

  c(name: string) {
    return this.form.get(name)!;
  }

  isInvalid(name: string) {
    const ctrl = this.c(name);
    return (this.submitted || ctrl.touched) && ctrl.invalid;
  }


  close() {
    this.router.navigate(['/profile']);
  }

  save() {
    this.submitted = true;
    this.form.markAllAsTouched();
    if (this.form.invalid) return;

    this.router.navigate(['/message', 'driver-profile-edit-submitted']);
  }
}
