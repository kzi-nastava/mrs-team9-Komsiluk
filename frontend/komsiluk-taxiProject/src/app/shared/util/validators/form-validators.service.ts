import { Injectable } from '@angular/core';
import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

@Injectable({
  providedIn: 'root',
})
export class FormValidatorsService {
  
}

export function matchFields(a: string, b: string): ValidatorFn {
  return (group: AbstractControl): ValidationErrors | null => {
    const av = group.get(a)?.value;
    const bv = group.get(b)?.value;
    return av === bv ? null : { mismatch: true };
  };
}