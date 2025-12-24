import { Component } from '@angular/core';
import { AuthCardComponent } from '../../components/auth-card/auth-card.component';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule, RouterLink } from '@angular/router';
import { trimRequired, strongPassword } from '../../../../shared/util/validators/field-validators.service';
import { matchFields } from '../../../../shared/util/validators/form-validators.service';
import { ToastService } from '../../../../shared/components/toast/toast.service';

@Component({
  selector: 'app-reset-password',
  imports: [AuthCardComponent, ReactiveFormsModule, RouterModule, RouterLink],
  templateUrl: './reset-password.component.html',
  styleUrl: './reset-password.component.css',
})
export class ResetPasswordComponent {
  submitted = false;

  form: ReturnType<FormBuilder['group']>;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private toast: ToastService
  ) {
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
    if (this.form.invalid) return;

    this.toast.show('Password changed successfully!');
    this.router.navigate(['/login']);
  }
}
