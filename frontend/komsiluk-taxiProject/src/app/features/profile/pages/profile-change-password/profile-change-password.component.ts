import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { ReactiveFormsModule, FormBuilder } from '@angular/forms';
import { ToastService } from '../../../../shared/components/toast/toast.service';
import { matchFields } from '../../../../shared/util/validators/form-validators.service';
import { trimRequired, strongPassword } from '../../../../shared/util/validators/field-validators.service';
import { ProfileService } from '../../services/profile.service';

@Component({
  selector: 'app-profile-change-password',
  imports: [ReactiveFormsModule],
  templateUrl: './profile-change-password.component.html',
  styleUrl: './profile-change-password.component.css',
})
export class ProfileChangePasswordComponent {
  submitted = false;
  saving = false;

  form: ReturnType<FormBuilder['group']>;

  constructor(
    private fb: FormBuilder,
    private toast: ToastService,
    private router: Router,
    private profileService: ProfileService
  ) {
    this.form = this.fb.group(
      {
        currentPassword: ['', [trimRequired]],
        password: ['', [trimRequired, strongPassword]],
        repeat: ['', [trimRequired]],
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

  close() {
    this.router.navigate(['/profile']);
  }

  save() {
    this.submitted = true;
    this.form.markAllAsTouched();
    if (this.form.invalid || this.saving) return;

    const currentPassword = this.c('currentPassword').value as string;
    const newPassword = this.c('password').value as string;

    this.saving = true;

    this.profileService.changeMyPassword(currentPassword, newPassword).subscribe({
      next: () => {
        this.toast.show('Password changed successfully!');
        this.router.navigate(['/profile']);
      },
      error: (err) => {
        if (err?.status === 400) {
          this.toast.show('Current password is incorrect.');
        } else {
          const msg = err?.error?.message || err?.error || 'Failed to change password. Please try again.';
          this.toast.show(msg);
        }
        this.saving = false;
      },
      complete: () => {
        this.saving = false;
      },
    });
  }
}