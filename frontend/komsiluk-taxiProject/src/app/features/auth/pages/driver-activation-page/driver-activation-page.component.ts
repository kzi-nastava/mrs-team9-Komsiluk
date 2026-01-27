import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

import { AuthCardComponent } from '../../components/auth-card/auth-card.component';
import { AuthService } from '../../../../core/auth/services/auth.service';
import { ToastService } from '../../../../shared/components/toast/toast.service';

type Step = 1 | 2;

@Component({
  selector: 'app-driver-activation-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, AuthCardComponent],
  templateUrl: './driver-activation-page.component.html',
  styleUrls: ['./driver-activation-page.component.css']
})
export class DriverActivationPageComponent implements OnInit {
  step = signal<Step>(1);
  loading = signal(false);

  token: string | null = null;

  form: FormGroup;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private fb: FormBuilder,
    private auth: AuthService,
    private toast: ToastService
  ) {
    this.form = this.fb.group({
      password: ['', [Validators.required, Validators.minLength(8), Validators.pattern(/^(?=.*[A-Za-z])(?=.*\d).+$/)]],
      repeat: ['', [Validators.required]],
    });
  }

  ngOnInit(): void {
    this.token = this.route.snapshot.queryParamMap.get('token');

    if (!this.token) {
      this.toast.show('Invalid activation link.');
      this.router.navigate(['']);
      return;
    }
  }

  c(name: string) {
    return this.form.get(name)!;
  }

  isInvalid(name: string) {
    const ctrl = this.c(name);
    return ctrl.invalid && (ctrl.dirty || ctrl.touched);
  }

  mismatch(): boolean {
    const p = this.c('password').value;
    const r = this.c('repeat').value;
    return !!p && !!r && p !== r;
  }

  submit() {
    this.form.markAllAsTouched();
    if (this.form.invalid || this.mismatch() || !this.token) return;

    this.loading.set(true);

    const password = String(this.c('password').value);

    this.auth.activateDriver(this.token, password).subscribe({
      next: () => {
        this.loading.set(false);
        this.step.set(2);

        this.router.navigate([], {
          queryParams: { token: null },
          queryParamsHandling: 'merge',
          replaceUrl: true
        });
      },
      error: (err) => {
        this.loading.set(false);
        const msg = err?.error?.message || 'Activation link is invalid or expired.';
        this.toast.show(msg);
        this.router.navigate(['']);
      }
    });
  }
}
