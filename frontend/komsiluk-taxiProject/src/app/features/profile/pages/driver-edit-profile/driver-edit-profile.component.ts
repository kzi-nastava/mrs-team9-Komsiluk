import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { trimRequired, personName, phoneNumber, licensePlate, seatsCount } from '../../../../shared/util/validators/field-validators.service';
import { ProfileService } from '../../services/profile.service';
import { ToastService } from '../../../../shared/components/toast/toast.service';
import { VehicleType, DriverEditRequestCreateDTO } from '../../../../shared/models/profile.models';

@Component({
  selector: 'app-driver-edit-profile',
  imports: [ReactiveFormsModule],
  templateUrl: './driver-edit-profile.component.html',
  styleUrl: './driver-edit-profile.component.css',
})
export class DriverEditProfileComponent implements OnInit {

  submitted = false;
  form: ReturnType<FormBuilder['group']>;

  constructor(private router: Router, private fb: FormBuilder, private profileService: ProfileService, private toast: ToastService, private cdr: ChangeDetectorRef) {
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

  ngOnInit(): void {
    this.profileService.getMyProfile().subscribe({
      next: (p: any) => {
        const v = p.vehicle;

        this.form.patchValue({
          // profil
          firstName: p.firstName ?? '',
          lastName: p.lastName ?? '',
          address: p.address ?? '',
          city: p.city ?? '',
          phone: p.phoneNumber ?? '',

          // car
          model: v?.model ?? '',
          type: this.vehicleTypeToUi(v?.type), // Standard/Van/Luxury
          licensePlate: (v?.licencePlate ?? v?.licensePlate) ?? '',
          seats: (v?.seatCount ?? '')?.toString?.() ?? v?.seatCount ?? '',

          petFriendly: !!v?.petFriendly,
          childSeatAvailable: !!v?.babyFriendly,
        });

        this.cdr.detectChanges();
      },
      error: () => {
        this.toast.show('Failed to load profile.');
      }
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

    const dto: DriverEditRequestCreateDTO = {
      newName: this.form.get('firstName')!.value,
      newSurname: this.form.get('lastName')!.value,
      newAddress: this.form.get('address')!.value,
      newCity: this.form.get('city')!.value,
      newPhoneNumber: this.form.get('phone')!.value,
      newProfileImageUrl: null, // for now

      newModel: this.form.get('model')!.value,
      newType: this.form.get('type')!.value as VehicleType,
      newLicencePlate: this.form.get('licensePlate')!.value,
      newSeatCount: Number(this.form.get('seats')!.value),

      newPetFriendly: !!this.form.get('petFriendly')!.value,
      newBabyFriendly: !!this.form.get('childSeatAvailable')!.value,
    };

    this.profileService.createDriverEditRequest(dto).subscribe({
      next: () => {
        this.router.navigate(['/message', 'driver-profile-edit-submitted']);
      },
      error: () => {
        this.toast.show('Failed to submit edit request.');
      }
    });
  }

  private vehicleTypeToUi(type: string | null | undefined): string {
    if (!type) return '';
    switch (type.toUpperCase()) {
      case 'STANDARD':
        return 'STANDARD';
      case 'VAN':
        return 'VAN';
      case 'LUXURY':
        return 'LUXURY';
      default:
        return '';
    }
  }
}
