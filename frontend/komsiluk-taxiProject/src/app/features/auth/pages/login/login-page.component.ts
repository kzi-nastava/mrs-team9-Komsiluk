import { Component } from '@angular/core';
import { AuthCardComponent } from '../../components/auth-card/auth-card.component';
import { ReactiveFormsModule, FormBuilder, Validators } from "@angular/forms";
import { ToastService } from '../../../../shared/components/toast/toast.service';
import { Router, RouterModule } from '@angular/router';
import { trimRequired, strongPassword } from '../../../../shared/util/validators/field-validators.service';
import { AuthService, UserRole } from '../../services/auth';

@Component({
  selector: 'app-login-page',
  imports: [AuthCardComponent, ReactiveFormsModule, RouterModule],
  templateUrl: './login-page.component.html',
  styleUrl: './login-page.component.css',
})
export class LoginPage {
  submitted = false;

  form: ReturnType<FormBuilder['group']>;
  authService: AuthService;

  constructor(
    private fb: FormBuilder,
    private toast: ToastService,
    private router: Router,
    authService: AuthService
  ) {
    this.authService = authService;
    this.form = this.fb.group(
      {
        password: ['', [trimRequired, strongPassword]],
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


    const success = this.authService.login(
      this.form.value.email,
      this.form.value.password
    );

    if (!success) {
      this.toast.show('Invalid credentials');
      return;
    }
    this.toast.show('Successful Log in!');
    this.router.navigateByUrl('/');
  }
}