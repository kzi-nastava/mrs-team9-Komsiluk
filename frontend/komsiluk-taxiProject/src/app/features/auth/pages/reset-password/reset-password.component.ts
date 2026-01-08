import { Component } from '@angular/core';
import { AuthCardComponent } from '../../components/auth-card/auth-card.component';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule, RouterLink } from '@angular/router';
import { trimRequired, strongPassword } from '../../../../shared/util/validators/field-validators.service';
import { matchFields } from '../../../../shared/util/validators/form-validators.service';
import { ToastService } from '../../../../shared/components/toast/toast.service';
import { AuthService } from '../../../../core/auth/services/auth.service';

@Component({
  selector: 'app-reset-password',
  imports: [AuthCardComponent, ReactiveFormsModule, RouterModule, RouterLink],
  templateUrl: './reset-password.component.html',
  styleUrl: './reset-password.component.css',
})
export class ResetPasswordComponent {
  submitted = false;
  loading = false;
  token: string | null = null;

  form: ReturnType<FormBuilder['group']>;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private auth: AuthService,
    private toast: ToastService
  ) {
    this.token = this.route.snapshot.queryParamMap.get('token');

    this.form = this.fb.group(
      {
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

  save() {
    this.submitted = true;
    this.form.markAllAsTouched();

    if (this.form.invalid || this.loading) return;

    if (!this.token) {
      this.toast.show('Invalid or expired reset link.');
      this.router.navigate(['/forgot-password']);
      return;
    }

    this.loading = true;

    this.auth.resetPassword({
      token: this.token,
      newPassword: this.c('password').value,
      confirmPassword: this.c('repeat').value,
    }).subscribe({
      next: () => {
        this.loading = false;
        this.toast.show('Password changed successfully!');
        this.router.navigate(['/login']);
      },
      error: () => {
        this.loading = false;
        this.toast.show('Reset link is invalid or expired.');
        this.router.navigate(['/forgot-password']);
      }
    });
  }
}
