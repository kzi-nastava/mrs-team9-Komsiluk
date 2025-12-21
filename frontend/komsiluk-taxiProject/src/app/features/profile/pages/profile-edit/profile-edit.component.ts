import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { ToastService } from '../../../../shared/components/toast/toast.service';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { trimRequired, personName, phoneNumber } from '../../../../shared/util/validators/field-validators.service';

@Component({
  selector: 'app-profile-edit',
  imports: [ReactiveFormsModule],
  templateUrl: './profile-edit.component.html',
  styleUrl: './profile-edit.component.css',
})
export class ProfileEditComponent {
  
  submitted = false;
  
  form!: any;
  
  constructor(private toast: ToastService, private router: Router, private fb: FormBuilder) {
    this.form = this.fb.group({
      firstName: ['', [trimRequired, Validators.minLength(2), personName]],
      lastName: ['', [trimRequired, Validators.minLength(2), personName]],
      address: ['', [trimRequired, Validators.minLength(5)]],
      city: ['', [trimRequired, Validators.minLength(2)]],
      phone: ['', [trimRequired, phoneNumber]],
    });
  }

  c(name: string) { return this.form.get(name)!; }
  show(name: string) { return (this.submitted || this.c(name).touched) && this.c(name).invalid; }

  close() {
    this.router.navigate(['/profile']);
  }

  save() {
    this.submitted = true;
    this.form.markAllAsTouched();
    if (this.form.invalid) return;

    this.toast.show('Profile updated successfully!');
    this.router.navigate(['/profile']);
  }

}