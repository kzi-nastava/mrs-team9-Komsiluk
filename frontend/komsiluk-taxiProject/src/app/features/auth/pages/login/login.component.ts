import { Component } from '@angular/core';
import { AuthCardComponent } from '../../components/auth-card/auth-card.component';
import { ReactiveFormsModule, FormBuilder, Validators } from "@angular/forms";
import { ToastService } from '../../../../shared/components/toast/toast.service';
import { Router, RouterModule } from '@angular/router';
import { trimRequired, strongPassword } from '../../../../shared/util/validators/field-validators.service';
import { AuthService } from '../../../../core/auth/services/auth.service';

@Component({
  selector: 'app-login',
  imports: [AuthCardComponent, ReactiveFormsModule, RouterModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent {
  submitted = false;
  form: ReturnType<FormBuilder['group']>;

  constructor(
    private fb: FormBuilder,
    private toast: ToastService,
    private router: Router,
    private authService: AuthService
  ) {
    this.form = this.fb.group(
      {
        password: ['', [trimRequired]],
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

    const { email, password } = this.form.value;

    this.authService.login(email, password).subscribe({
      next: () => {
        this.toast.show('Successful Log in!');
        this.router.navigateByUrl('/');
      },
      error: () => {
        this.toast.show('Invalid credentials');
      }
    });
  }

}