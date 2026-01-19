import { Component } from '@angular/core';
import { AuthCardComponent } from '../../components/auth-card/auth-card.component';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { trimRequired } from '../../../../shared/util/validators/field-validators.service';
import { ToastService } from '../../../../shared/components/toast/toast.service';
import { AuthService } from '../../../../core/auth/services/auth.service';

@Component({
  selector: 'app-forgot-password',
  imports: [AuthCardComponent, ReactiveFormsModule, RouterModule],
  templateUrl: './forgot-password.component.html',
  styleUrl: './forgot-password.component.css',
})
export class ForgotPasswordComponent {
  submitted = false;
  loading = false;

  form: ReturnType<FormBuilder['group']>;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private auth: AuthService,
    private toast: ToastService
  ) {
    this.form = this.fb.group({
      email: ['', [trimRequired, Validators.email]],
    });
  }

  c(name: string) {
    return this.form.get(name)!;
  }

  isInvalid(name: string) {
    const ctrl = this.c(name);
    return (this.submitted || ctrl.touched) && ctrl.invalid;
  }

  save() {
    this.submitted = true;
    this.form.markAllAsTouched();
    if (this.form.invalid || this.loading) return;

    this.loading = true;

    const email = this.c('email').value;

    this.auth.forgotPassword(email).subscribe({
      next: () => {
        this.loading = false;
        this.router.navigate(['/forgot-password-message'], {
          queryParams: { email },
        });
      },
      error: (err) => {
        this.loading = false;

        const message = err?.error?.message;

        if (message) {
          this.toast.show(message);
        } else {
          this.toast.show('Something went wrong. Please try again later.');
        }
      }
    });
  }
}
