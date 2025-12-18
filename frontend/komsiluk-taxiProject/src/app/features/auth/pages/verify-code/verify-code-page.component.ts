import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthCardComponent } from '../../components/auth-card/auth-card.component';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from "@angular/material/input";
import {MatButtonModule} from "@angular/material/button";
import {FormControl, FormGroup, Validators, ReactiveFormsModule} from "@angular/forms";
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-verify-code-page',
  imports: [AuthCardComponent,MatFormFieldModule, ReactiveFormsModule, MatInputModule, MatButtonModule,CommonModule,RouterModule],
  templateUrl: './verify-code-page.component.html',
  styleUrl: './verify-code-page.component.css',
})
export class VerifyCodePage {
  VerifyCodeForm: FormGroup = new FormGroup({
      verificationCode: new FormControl('', [Validators.required, Validators.minLength(6), Validators.maxLength(6)]),
    });

    onSubmit() {
      if (this.VerifyCodeForm.invalid) {
        this.VerifyCodeForm.markAllAsTouched();
        return;
      }

      console.log(this.VerifyCodeForm.value);
    }
}
