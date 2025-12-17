import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthCardComponent } from '../../components/auth-card/auth-card.component';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from "@angular/material/input";
import {MatButtonModule} from "@angular/material/button";
import {FormControl, FormGroup, Validators, ReactiveFormsModule} from "@angular/forms";
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-login-page',
  imports: [AuthCardComponent,MatFormFieldModule, ReactiveFormsModule, MatInputModule, MatButtonModule,CommonModule,RouterModule],
  templateUrl: './login-page.component.html',
  styleUrl: './login-page.component.css',
})
export class LoginPage {
  LoginForm: FormGroup = new FormGroup({
      email: new FormControl('', [Validators.required, Validators.email]),
      password: new FormControl('', [Validators.required, Validators.minLength(6)]),
    });

    constructor(private router: Router) {

    }

    onSubmit() {
      if (this.LoginForm.invalid) {
        this.LoginForm.markAllAsTouched();
        return;
      }

      this.router.navigate(['/']);
    }
}