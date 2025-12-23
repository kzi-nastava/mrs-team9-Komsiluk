import { Component } from '@angular/core';
import { AuthCardComponent } from '../../components/auth-card/auth-card.component';
import {FormBuilder, Validators, ReactiveFormsModule} from "@angular/forms";
import { Router, RouterModule } from '@angular/router';
import { trimRequired } from '../../../../shared/util/validators/field-validators.service';

@Component({
  selector: 'app-forgot-password',
  imports: [AuthCardComponent, ReactiveFormsModule, RouterModule],
  templateUrl: './forgot-password.component.html',
  styleUrl: './forgot-password.component.css',
})
export class ForgotPasswordPage {
  submitted = false;

  form: ReturnType<FormBuilder['group']>;

  constructor(
    private fb: FormBuilder,
    private router: Router
  ) {
    this.form = this.fb.group(
      {
        email: ['', [trimRequired, Validators.email]],
      }
    );
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
    if (this.form.invalid) return;

    this.router.navigate(['/activation-message']);
  }
}
