import { Component } from '@angular/core';
import { AuthCardComponent } from '../../components/auth-card/auth-card.component';
import { ReactiveFormsModule, FormBuilder, Validators } from "@angular/forms";
import { ToastService } from '../../../../shared/components/toast/toast.service';
import { Router, RouterModule } from '@angular/router';
import { trimRequired } from '../../../../shared/util/validators/field-validators.service';
import { AuthService, UserRole } from '../../../../core/auth/services/auth.service';
import { DriverService } from '../../../../core/layout/components/driver/services/driver.service';
import { DriverRuntimeStateService } from '../../../../core/layout/components/driver/services/driver-runtime-state.service';

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
    private authService: AuthService,
    private driverApi: DriverService,
    private driverState: DriverRuntimeStateService
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
        const id = Number(this.authService.userId());
        const role = this.authService.userRole();
        const s = this.driverState.status();
    
        const shouldGoActive = role === UserRole.DRIVER && id && (s === 'INACTIVE');
    
        if (shouldGoActive) {
          this.driverApi.updateStatus(id, 'ACTIVE').subscribe({
            next: () => {
              this.driverState.setStatus('ACTIVE');
              this.toast.show('Successful Log in!');
              this.router.navigateByUrl('/');
            },
            error: () => {
              this.toast.show('Successful Log in!');
              this.router.navigateByUrl('/');
            }
          });
          return;
        }

        this.toast.show('Successful Log in!');
        this.router.navigateByUrl('/');
      },
      error: (err) => {
        if (err.status === 400 && err.error?.message) {
          this.toast.show(err.error.message);
        } else {
          this.toast.show('Invalid credentials');
        }
      }
    });
  }

}