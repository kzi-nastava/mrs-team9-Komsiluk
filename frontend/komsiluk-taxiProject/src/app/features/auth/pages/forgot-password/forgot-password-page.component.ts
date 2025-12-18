import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthCardComponent } from '../../components/auth-card/auth-card.component';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from "@angular/material/input";
import {MatButtonModule} from "@angular/material/button";
import {FormControl, FormGroup, Validators, ReactiveFormsModule} from "@angular/forms";
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-forgot-password-page',
  imports: [AuthCardComponent,MatFormFieldModule, ReactiveFormsModule, MatInputModule, MatButtonModule,CommonModule, RouterModule],
  templateUrl: './forgot-password-page.component.html',
  styleUrl: './forgot-password-page.component.css',
})
export class ForgotPasswordPage {
  ForgotPasswordForm: FormGroup = new FormGroup({
      email: new FormControl('', [Validators.required, Validators.email]),
      password: new FormControl('', [Validators.required, Validators.minLength(6)]),
    });

    constructor(private router: Router) {

    }

    onSubmit() {
      if (this.ForgotPasswordForm.invalid) {
        this.ForgotPasswordForm.markAllAsTouched();
        return;
      }

      this.router.navigate(['/verify-recovery-code']);
    }
}
