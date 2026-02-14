import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { AdminDriverRegistrationDriverPanelComponent } from './admin-driver-registration-driver-panel.component';

describe('AdminDriverRegistrationDriverPanelComponent', () => {
  let component: AdminDriverRegistrationDriverPanelComponent;
  let fixture: ComponentFixture<AdminDriverRegistrationDriverPanelComponent>;

  function createForm(): FormGroup {
    return new FormGroup({
      firstName: new FormControl('', [Validators.required, Validators.minLength(2), Validators.maxLength(50)]),
      lastName: new FormControl('', [Validators.required, Validators.minLength(2), Validators.maxLength(50)]),
      address: new FormControl('', [Validators.required, Validators.minLength(5), Validators.maxLength(100)]),
      city: new FormControl('', [Validators.required, Validators.minLength(2), Validators.maxLength(50)]),
      phoneNumber: new FormControl('', [Validators.required]),
      email: new FormControl('', [Validators.required, Validators.email]),
      profilePhoto: new FormControl<File | null>(null),
    });
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminDriverRegistrationDriverPanelComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(AdminDriverRegistrationDriverPanelComponent);
    component = fixture.componentInstance;

    component.form = createForm();

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render all driver inputs (firstName, lastName, address, city, phoneNumber, email) and file input', () => {
    const el: HTMLElement = fixture.nativeElement;

    expect(el.querySelector('input[formControlName="firstName"]')).toBeTruthy();
    expect(el.querySelector('input[formControlName="lastName"]')).toBeTruthy();
    expect(el.querySelector('input[formControlName="address"]')).toBeTruthy();
    expect(el.querySelector('input[formControlName="city"]')).toBeTruthy();
    expect(el.querySelector('input[formControlName="phoneNumber"]')).toBeTruthy();
    expect(el.querySelector('input[formControlName="email"]')).toBeTruthy();
    expect(el.querySelector('input[type="file"]')).toBeTruthy();
  });

  it('should show error message for firstName when it is invalid and touched', () => {
    const ctrl = component.form.get('firstName')!;
    ctrl.setValue('');
    ctrl.markAsTouched();

    fixture.detectChanges();

    const el: HTMLElement = fixture.nativeElement;

    const err = Array.from(el.querySelectorAll('.form-error')).find(e => (e.textContent || '').includes('2–50 characters'));

    expect(err).toBeTruthy();
  });

  it('should NOT show firstName error if invalid but untouched', () => {
    component.form.get('firstName')!.setValue('');

    fixture.detectChanges();

    const el: HTMLElement = fixture.nativeElement;
    const hasFirstNameErr = Array.from(el.querySelectorAll('.form-error')).some(e => (e.textContent || '').includes('2–50 characters'));

    expect(hasFirstNameErr).toBeFalse();
  });

  it('should show "Email is required" when email is empty and touched', () => {
    const email = component.form.get('email')!;
    email.setValue('');
    email.markAsTouched();

    fixture.detectChanges();

    const el: HTMLElement = fixture.nativeElement;
    expect(el.textContent || '').toContain('Email is required.');
  });

  it('should show "Enter a valid email." when email is invalid and touched', () => {
    const email = component.form.get('email')!;
    email.setValue('aaa');
    email.markAsTouched();

    fixture.detectChanges();

    const el: HTMLElement = fixture.nativeElement;
    expect(el.textContent || '').toContain('Enter a valid email.');
  });

  it('should patch profilePhoto when user selects a file and mark it as touched', () => {
    const file = new File(['x'], 'avatar.png', { type: 'image/png' });

    const fileInputDe = fixture.debugElement.query(By.css('input[type="file"]'));
    const inputEl = fileInputDe.nativeElement as HTMLInputElement;

    Object.defineProperty(inputEl, 'files', {
      value: [file],
    });

    inputEl.dispatchEvent(new Event('change'));
    fixture.detectChanges();

    const ctrl = component.form.get('profilePhoto')!;
    expect(ctrl.value).toBe(file);
    expect(ctrl.touched).toBeTrue();

    const labelText = (fixture.nativeElement as HTMLElement).textContent || '';
    expect(labelText).toContain('avatar.png');
  });

  it('should emit next when Next button is clicked', () => {
    spyOn(component.next, 'emit');

    const btnDe = fixture.debugElement.query(By.css('button.btn--primary'));
    btnDe.triggerEventHandler('click', null);

    expect(component.next.emit).toHaveBeenCalled();
  });

  it('should keep profilePhoto null if user did not select a file', () => {
    const fileInputDe = fixture.debugElement.query(By.css('input[type="file"]'));
    const inputEl = fileInputDe.nativeElement as HTMLInputElement;

    Object.defineProperty(inputEl, 'files', { value: [] });
    inputEl.dispatchEvent(new Event('change'));

    fixture.detectChanges();

    expect(component.form.get('profilePhoto')!.value).toBeNull();
  });
});
