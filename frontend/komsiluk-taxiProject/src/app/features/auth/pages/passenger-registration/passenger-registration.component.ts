import { Component } from '@angular/core';
import { AuthCardComponent } from '../../components/auth-card/auth-card.component';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import {
  authPersonName,
  personName,
  phoneNumber,
  strongPassword,
  trimRequired,
} from '../../../../shared/util/validators/field-validators.service';
import { matchFields } from '../../../../shared/util/validators/form-validators.service';
import { ToastService } from '../../../../shared/components/toast/toast.service';
import { AuthService } from '../../../../core/auth/services/auth.service';
import { finalize } from 'rxjs';

@Component({
  selector: 'app-passenger-registration.component',
  imports: [AuthCardComponent, ReactiveFormsModule, RouterModule],
  templateUrl: './passenger-registration.component.html',
  styleUrl: './passenger-registration.component.css',
})
export class PassengerRegistrationComponent {
  submitted = false;
  loading = false;

  form: ReturnType<FormBuilder['group']>;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private auth: AuthService,
    private toast: ToastService
  ) {
    this.form = this.fb.group(
      {
        firstName: ['', [trimRequired, authPersonName]],
        lastName: ['', [trimRequired, authPersonName]],
        address: ['', [trimRequired, Validators.minLength(5), Validators.maxLength(100)]],
        city: ['', [trimRequired, Validators.minLength(2), Validators.maxLength(50)]],
        phone: ['', [trimRequired, phoneNumber]],
        email: ['', [trimRequired, Validators.email]],
        password: ['', [trimRequired, strongPassword]],
        repeat: ['', [trimRequired]],
        profilePhoto: [null],
      },
      { validators: [matchFields('password', 'repeat')] }
    );
  }

  c(name: string) {
    return this.form.get(name)!;
  }

  isInvalid(name: string) {
    const ctrl = this.c(name);
    return (this.submitted || ctrl.touched) && ctrl.invalid;
  }

  mismatch() {
    return (this.submitted || this.form.touched) && this.form.hasError('mismatch');
  }

  save() {
    this.submitted = true;
    this.form.markAllAsTouched();
    if (this.form.invalid) return;

    if (this.loading) return;
    this.loading = true;

    const payload = {
      firstName: this.c('firstName').value,
      lastName: this.c('lastName').value,
      address: this.c('address').value,
      city: this.c('city').value,
      phoneNumber: this.c('phone').value,
      email: this.c('email').value,
      password: this.c('password').value,
      confirmPassword: this.c('repeat').value
    };

    const formData = new FormData();
    formData.append('data', new Blob([JSON.stringify(payload)], { type: 'application/json' }));

    const file = this.c('profilePhoto').value;
    if (file) {
      formData.append('profileImage', file);
    }

    this.auth
      .registerPassenger(formData)
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: () => {
          this.toast.show('Activation link has been sent to your email.');

          this.router.navigate(['/activation-message'], {
            queryParams: { email: payload.email },
          });
        },
        error: (err: { status: number; }) => {
          if (err?.status === 409) {
            this.toast.show('Email already exists.');
            return;
          }

          if (err?.status === 400) {
            this.toast.show('Invalid data. Please check the form.');
            return;
          }

          if (err?.status === 403) {
            this.toast.show('Account is not activated. Please check your email.');
            return;
          }
          this.toast.show('Something went wrong. Please try again.');
        },
      });
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) return;

    const file = input.files[0];

    if (!file.type.startsWith('image/')) {
      this.toast.show('Please select an image file.');
      return;
    }
    if (file.size > 8 * 1024 * 1024) {
      this.toast.show('Image is too large (max 8MB).');
      return;
    }

    this.form.patchValue({
      profilePhoto: file,
    });
    this.form.get('profilePhoto')?.markAsTouched();
  }

}
