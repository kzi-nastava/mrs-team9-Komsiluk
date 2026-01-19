import { Injectable } from '@angular/core';
import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

@Injectable({
  providedIn: 'root',
})
export class FieldValidatorsService {
  
}

export const trimRequired: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
  const v = String(control.value ?? '').trim();
  return v.length ? null : { required: true };
};

// Name\Surname: min 2 letters, just letters, space, dash, apostrophe
export const personName: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
  const v = String(control.value ?? '').trim();
  if (!v) return null; // required rešava prazno
  const ok = /^[\p{L}][\p{L}\s'-]{1,}$/u.test(v);
  return ok ? null : { name: true };
};

// Name\Surname: min 2 letters,max 50 letters, just letters, space, dash, apostrophe
export const authPersonName: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
  const v = String(control.value ?? '').trim();
  if (!v) return null; // required rešava prazno
  const ok = /^[\p{L}][\p{L}\s'-]{1,49}$/u.test(v);
  return ok ? null : { name: true };
};

// Phone number: digits, optional + at start, length 7-15
export const phoneNumber: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
  const v = String(control.value ?? '').trim();
  if (!v) return null;
  return /^\+?\d{7,15}$/.test(v) ? null : { phone: true };
};

// License plates like NS-111-AB (also allows ns-111-ab, convert to uppercase on save)
export const licensePlate: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
  const v = String(control.value ?? '').trim();
  if (!v) return null;
  return /^[A-Za-z]{2}-\d{3}-[A-Za-z]{2}$/.test(v) ? null : { plate: true };
};

// Seats count: integer 1-8
export const seatsCount: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
  const v = String(control.value ?? '').trim();
  if (!v) return null;
  const n = Number(v);
  if (!Number.isInteger(n)) return { seats: true };
  if (n < 1 || n > 8) return { seatsRange: true };
  return null;
};

// Password: min 8, at least 1 letter and 1 number (can include special characters)
export const strongPassword: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
  const v = String(control.value ?? '');
  if (!v) return null;
  const ok = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d@$!%*#?&]{8,}$/.test(v);
  return ok ? null : { password: true };
};