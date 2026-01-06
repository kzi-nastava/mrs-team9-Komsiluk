import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ToastService } from '../../../../shared/components/toast/toast.service';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { trimRequired, personName, phoneNumber } from '../../../../shared/util/validators/field-validators.service';
import { ProfileService } from '../../services/profile.service';
import { UserProfileUpdateDTO } from '../../../../shared/models/profile.models';

@Component({
  selector: 'app-profile-edit',
  imports: [ReactiveFormsModule],
  templateUrl: './profile-edit.component.html',
  styleUrl: './profile-edit.component.css',
})
export class ProfileEditComponent implements OnInit {
  
  submitted = false;
  form!: any;
  
  constructor(private toast: ToastService, private router: Router, private fb: FormBuilder, private profileService: ProfileService) {
    this.form = this.fb.group({
      firstName: ['', [trimRequired, Validators.minLength(2), personName]],
      lastName: ['', [trimRequired, Validators.minLength(2), personName]],
      address: ['', [trimRequired, Validators.minLength(5)]],
      city: ['', [trimRequired, Validators.minLength(2)]],
      phone: ['', [trimRequired, phoneNumber]],
    });
  }

   ngOnInit(): void {
    this.profileService.getMyProfile().subscribe({
      next: (p) => {
        this.form.patchValue({
          firstName: p.firstName ?? '',
          lastName: p.lastName ?? '',
          address: p.address ?? '',
          city: p.city ?? '',
          phone: p.phoneNumber ?? '',
        });
      },
      error: () => {
        this.toast.show('Failed to load profile.');
      }
    });
  }

  c(name: string) { return this.form.get(name)!; }
  show(name: string) { return (this.submitted || this.c(name).touched) && this.c(name).invalid; }

  close() {
    this.router.navigate(['/profile']);
  }

  save() {
    this.submitted = true;
    this.form.markAllAsTouched();
    if (this.form.invalid) return;

    const dto: UserProfileUpdateDTO = {
      firstName: this.form.value.firstName,
      lastName: this.form.value.lastName,
      address: this.form.value.address,
      city: this.form.value.city,
      phoneNumber: this.form.value.phone,
    };

    this.profileService.updateProfile(dto).subscribe({
      next: () => {
        this.toast.show('Profile updated successfully!');
        this.router.navigate(['/profile']);
      },
      error: () => {
        this.toast.show('Update failed. Please try again.');
      }
    });
  }

}