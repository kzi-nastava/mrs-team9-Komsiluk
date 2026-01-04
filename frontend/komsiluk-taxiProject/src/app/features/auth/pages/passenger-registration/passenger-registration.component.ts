import { Component } from '@angular/core';
import { AuthCardComponent } from '../../components/auth-card/auth-card.component';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { personName, phoneNumber, strongPassword, trimRequired } from '../../../../shared/util/validators/field-validators.service';
import { matchFields } from '../../../../shared/util/validators/form-validators.service';

@Component({
  selector: 'app-passenger-registration.component',
  imports: [AuthCardComponent, ReactiveFormsModule, RouterModule],
  templateUrl: './passenger-registration.component.html',
  styleUrl: './passenger-registration.component.css',
})
export class PassengerRegistrationComponent {
  submitted = false;
  selectedFileName = '';
  form: ReturnType<FormBuilder['group']>;

  constructor(
    private fb: FormBuilder,
    private router: Router
  ) {
    this.form = this.fb.group(
      {
        firstName: ['', [trimRequired, Validators.minLength(2), personName]],
        lastName: ['', [trimRequired, Validators.minLength(2), personName]],
        address: ['', [trimRequired, Validators.minLength(5)]],
        city: ['', [trimRequired, Validators.minLength(2)]],
        phone: ['', [trimRequired, phoneNumber]],
        email: ['', [trimRequired, Validators.email]],
        password: ['', [trimRequired, strongPassword]],
        repeat: ['', [trimRequired]],
        profilePhoto: [null],
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

    this.router.navigate(['/activation-message']);
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;

    if (!input.files || input.files.length === 0) {
      return;
    }

    const file = input.files[0];

    this.form.patchValue({
      profilePhoto: file
    });

    this.form.get('profilePhoto')?.markAsTouched();
  }

}
