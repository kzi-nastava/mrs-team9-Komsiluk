import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PassengerRegistrationComponent } from './passenger-registration.component';
import { AuthService } from '../../../../core/auth/services/auth.service';
import { ToastService } from '../../../../shared/components/toast/toast.service';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { ReactiveFormsModule } from '@angular/forms';

describe('PassengerRegistrationComponent', () => {
  let component: PassengerRegistrationComponent;
  let fixture: ComponentFixture<PassengerRegistrationComponent>;

  let authServiceMock: jasmine.SpyObj<AuthService>;
  let toastMock: jasmine.SpyObj<ToastService>;
  let routerMock: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    authServiceMock = jasmine.createSpyObj<AuthService>('AuthService', ['registerPassenger']);
    toastMock = jasmine.createSpyObj<ToastService>('ToastService', ['show']);
    routerMock = jasmine.createSpyObj<Router>('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [PassengerRegistrationComponent, ReactiveFormsModule],
    }).compileComponents();

    TestBed.overrideProvider(AuthService, { useValue: authServiceMock });
    TestBed.overrideProvider(ToastService, { useValue: toastMock });
    TestBed.overrideProvider(Router, { useValue: routerMock });

    fixture = TestBed.createComponent(PassengerRegistrationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('save should NOT call registerPassenger if form is invalid', () => {
    component.save();
    expect(authServiceMock.registerPassenger).not.toHaveBeenCalled();
    expect(component.submitted).toBeTrue();
  });

  it('save should call registerPassenger with correct FormData when valid', () => {
    const validData = {
      firstName: 'John',
      lastName: 'Doe',
      address: 'Bulevar Oslobodjenja 123',
      city: 'Novi Sad',
      phone: '+38164123456',
      email: 'john@example.com',
      password: 'StrongPassword123!',
      repeat: 'StrongPassword123!'
    };
    component.form.patchValue(validData);
    authServiceMock.registerPassenger.and.returnValue(of(undefined));

    component.save();

    expect(authServiceMock.registerPassenger).toHaveBeenCalled();
    
    const formDataArg = authServiceMock.registerPassenger.calls.mostRecent().args[0] as FormData;
    expect(formDataArg.has('data')).toBeTrue();
  });

  it('save should handle successful registration and navigate', () => {
    component.form.patchValue({
      firstName: 'John', lastName: 'Doe', address: 'Address 123',
      city: 'City', phone: '1234567', email: 'test@test.com',
      password: 'Password123!', repeat: 'Password123!'
    });
    authServiceMock.registerPassenger.and.returnValue(of(undefined));

    component.save();

    expect(toastMock.show).toHaveBeenCalledWith('Activation link has been sent to your email.');
    expect(routerMock.navigate).toHaveBeenCalledWith(['/activation-message'], {
      queryParams: { email: 'test@test.com' }
    });
  });

  it('save should handle 409 Email already exists error', () => {
    component.form.patchValue({
      firstName: 'John', lastName: 'Doe', address: 'Address 123',
      city: 'City', phone: '1234567', email: 'exists@test.com',
      password: 'Password123!', repeat: 'Password123!'
    });
    authServiceMock.registerPassenger.and.returnValue(throwError(() => ({ status: 409 })));

    component.save();

    expect(toastMock.show).toHaveBeenCalledWith('Email already exists.');
    expect(component.loading).toBeFalse();
  });

  it('onFileSelected should patch form with valid image', () => {
    const file = new File([''], 'test.png', { type: 'image/png' });
    const event = { target: { files: [file] } } as any;

    component.onFileSelected(event);

    expect(component.form.get('profilePhoto')?.value).toBe(file);
  });

  it('onFileSelected should show error for invalid file type', () => {
    const file = new File([''], 'test.txt', { type: 'text/plain' });
    const event = { target: { files: [file] } } as any;

    component.onFileSelected(event);

    expect(toastMock.show).toHaveBeenCalledWith('Please select an image file.');
    expect(component.form.get('profilePhoto')?.value).toBeNull();
  });

  it('onFileSelected should show error for too large file', () => {
    const largeFile = new File([''], 'big.png', { type: 'image/png' });
    Object.defineProperty(largeFile, 'size', { value: 9 * 1024 * 1024 });
    const event = { target: { files: [largeFile] } } as any;

    component.onFileSelected(event);

    expect(toastMock.show).toHaveBeenCalledWith('Image is too large (max 8MB).');
  });

  it('mismatch should return true when passwords do not match', () => {
    component.form.patchValue({
      password: 'Password123!',
      repeat: 'DifferentPassword123!'
    });
    component.form.markAsTouched();
    
    expect(component.mismatch()).toBeTrue();
  });
});