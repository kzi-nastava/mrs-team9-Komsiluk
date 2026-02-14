import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';

import { AdminDriverRegistrationPageComponent } from './admin-driver-registration-page.component';
import { DriverService } from '../services/driver.service';
import { ToastService } from '../../../shared/components/toast/toast.service';
import { DriverCreateDTO } from '../../../shared/models/driver.models';

describe('AdminDriverRegistrationPageComponent', () => {
  let component: AdminDriverRegistrationPageComponent;
  let fixture: ComponentFixture<AdminDriverRegistrationPageComponent>;

  let driverServiceMock: jasmine.SpyObj<DriverService>;
  let toastMock: jasmine.SpyObj<ToastService>;

  beforeEach(async () => {
    driverServiceMock = jasmine.createSpyObj<DriverService>('DriverService', ['registerDriver']);
    toastMock = jasmine.createSpyObj<ToastService>('ToastService', ['show']);

    await TestBed.configureTestingModule({
      imports: [AdminDriverRegistrationPageComponent],
    }).compileComponents();

    TestBed.overrideProvider(DriverService, { useValue: driverServiceMock });
    TestBed.overrideProvider(ToastService, { useValue: toastMock });

    fixture = TestBed.createComponent(AdminDriverRegistrationPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('goNextFromDriver should NOT advance if driver form is invalid', () => {
    expect(component.step()).toBe(1);

    component.goNextFromDriver();

    expect(component.step()).toBe(1);
  });

  it('goNextFromDriver should advance to step 2 when driver form is valid', () => {
    component.driverForm.patchValue({
      firstName: 'Nikola',
      lastName: 'Savic',
      address: 'Bulevar 1',
      city: 'Novi Sad',
      phoneNumber: '+381641234567',
      email: 'nikola@example.com',
      profilePhoto: null,
    });

    expect(component.driverForm.valid).toBeTrue();

    component.goNextFromDriver();

    expect(component.step()).toBe(2);
  });

  it('createDriver should call DriverService.registerDriver with correct DTO and null file (no photo)', () => {
    component.driverForm.patchValue({
      firstName: 'Driver',
      lastName: 'Test',
      address: 'Address 123',
      city: 'City',
      phoneNumber: '12345678',
      email: 'email@example.com',
      profilePhoto: null,
    });

    component.vehicleForm.patchValue({
      model: 'Car Model',
      type: 'STANDARD',
      licencePlate: 'AA-123-AA',
      seatCount: 4,
      petFriendly: true,
      babyFriendly: false,
    });

    driverServiceMock.registerDriver.and.returnValue(of({
      id: 1,
      email: 'email@example.com',
      firstName: 'Driver',
      lastName: 'Test',
      address: 'Address 123',
      city: 'City',
      phoneNumber: '12345678',
      profileImageUrl: '',
      active: false,
      blocked: false,
      createdAt: '',
      role: 'DRIVER',
      driverStatus: 'INACTIVE',
      vehicle: {}
    }));

    component.step.set(2);

    component.createDriver();

    expect(driverServiceMock.registerDriver).toHaveBeenCalledTimes(1);

    const [dtoArg, fileArg] = driverServiceMock.registerDriver.calls.mostRecent().args as [DriverCreateDTO, File | null];

    expect(dtoArg.firstName).toBe('Driver');
    expect(dtoArg.lastName).toBe('Test');
    expect(dtoArg.address).toBe('Address 123');
    expect(dtoArg.city).toBe('City');
    expect(dtoArg.phoneNumber).toBe('12345678');
    expect(dtoArg.email).toBe('email@example.com');

    expect(dtoArg.vehicle.model).toBe('Car Model');
    expect(dtoArg.vehicle.type).toBe('STANDARD');
    expect(dtoArg.vehicle.licencePlate).toBe('AA-123-AA');
    expect(dtoArg.vehicle.seatCount).toBe(4);
    expect(dtoArg.vehicle.petFriendly).toBeTrue();
    expect(dtoArg.vehicle.babyFriendly).toBeFalse();

    expect(fileArg).toBeNull();

    expect(component.step()).toBe(3);
    expect(component.creating()).toBeFalse();
  });

  it('createDriver should pass selected profile image when provided', () => {
    const file = new File(['x'], 'avatar.png', { type: 'image/png' });

    component.driverForm.patchValue({
      firstName: 'Driver',
      lastName: 'Test',
      address: 'Address 123',
      city: 'City',
      phoneNumber: '12345678',
      email: 'email@example.com',
      profilePhoto: file,
    });

    component.vehicleForm.patchValue({
      model: 'Car Model',
      type: 'STANDARD',
      licencePlate: 'AA-123-AA',
      seatCount: 4,
      petFriendly: false,
      babyFriendly: true,
    });

    driverServiceMock.registerDriver.and.returnValue(of({
      id: 1,
      email: 'email@example.com',
      firstName: 'Driver',
      lastName: 'Test',
      address: 'Address 123',
      city: 'City',
      phoneNumber: '12345678',
      profileImageUrl: '',
      active: false,
      blocked: false,
      createdAt: '',
      role: 'DRIVER',
      driverStatus: 'INACTIVE',
      vehicle: {}
    }));

    component.step.set(2);
    component.createDriver();

    const [, fileArg] = driverServiceMock.registerDriver.calls.mostRecent().args as [DriverCreateDTO, File | null];
    expect(fileArg).toBe(file);
  });

  it('createDriver should show toast on error and stay on step 2', () => {
    component.driverForm.patchValue({
      firstName: 'Driver',
      lastName: 'Test',
      address: 'Address 123',
      city: 'City',
      phoneNumber: '12345678',
      email: 'email@example.com',
      profilePhoto: null,
    });

    component.vehicleForm.patchValue({
      model: 'Car Model',
      type: 'STANDARD',
      licencePlate: 'AA-123-AA',
      seatCount: 4,
      petFriendly: false,
      babyFriendly: false,
    });

    driverServiceMock.registerDriver.and.returnValue(
      throwError(() => ({ error: { message: 'Driver registration failed.' } }))
    );

    component.step.set(2);

    component.createDriver();

    expect(toastMock.show).toHaveBeenCalledWith('Driver registration failed.');
    expect(component.step()).toBe(2);
    expect(component.creating()).toBeFalse();
  });

  it('done should reset forms and return to step 1', () => {
    component.step.set(3);
    component.driverForm.patchValue({
      firstName: 'A',
      lastName: 'B',
      address: 'C',
      city: 'D',
      phoneNumber: '123',
      email: 'a@b.com',
      profilePhoto: new File(['x'], 'a.png', { type: 'image/png' }),
    });
    component.vehicleForm.patchValue({
      model: 'X',
      type: 'LUXURY',
      licencePlate: 'AA-123-AA',
      seatCount: 2,
      petFriendly: true,
      babyFriendly: true,
    });

    component.done();

    expect(component.step()).toBe(1);

    expect(component.driverForm.value.firstName).toBe('');
    expect(component.driverForm.value.lastName).toBe('');
    expect(component.driverForm.value.address).toBe('');
    expect(component.driverForm.value.city).toBe('');
    expect(component.driverForm.value.phoneNumber).toBe('');
    expect(component.driverForm.value.email).toBe('');
    expect(component.driverForm.value.profilePhoto).toBeNull();

    expect(component.vehicleForm.value.model).toBe('');
    expect(component.vehicleForm.value.type).toBe('STANDARD');
    expect(component.vehicleForm.value.licencePlate).toBe('');
    expect(component.vehicleForm.value.seatCount).toBe('');
    expect(component.vehicleForm.value.petFriendly).toBeFalse();
    expect(component.vehicleForm.value.babyFriendly).toBeFalse();
  });

  it('createDriver should NOT call service when vehicle form is invalid', () => {
    component.driverForm.patchValue({
      firstName: 'Driver',
      lastName: 'Test',
      address: 'Address 123',
      city: 'City',
      phoneNumber: '12345678',
      email: 'email@example.com',
      profilePhoto: null,
    });
    expect(component.driverForm.valid).toBeTrue();

    component.vehicleForm.reset({
      model: '',
      type: 'STANDARD',
      licencePlate: '',
      seatCount: '',
      petFriendly: false,
      babyFriendly: false,
    });
    expect(component.vehicleForm.invalid).toBeTrue();

    component.step.set(2);
    component.createDriver();

    expect(driverServiceMock.registerDriver).not.toHaveBeenCalled();
    expect(component.step()).toBe(2);
  });

  it('createDriver should use string error body when backend returns error as string', () => {
    component.driverForm.patchValue({
      firstName: 'Driver',
      lastName: 'Test',
      address: 'Address 123',
      city: 'City',
      phoneNumber: '12345678',
      email: 'email@example.com',
      profilePhoto: null,
    });

    component.vehicleForm.patchValue({
      model: 'Car Model',
      type: 'STANDARD',
      licencePlate: 'AA-123-AA',
      seatCount: 4,
      petFriendly: false,
      babyFriendly: false,
    });

    driverServiceMock.registerDriver.and.returnValue(
      throwError(() => ({ error: 'Email already exists.' }))
    );

    component.step.set(2);
    component.createDriver();

    expect(toastMock.show).toHaveBeenCalledWith('Email already exists.');
    expect(component.step()).toBe(2);
    expect(component.creating()).toBeFalse();
  });

  it('createDriver should show default message when backend returns unknown error shape', () => {
    component.driverForm.patchValue({
      firstName: 'Nikola',
      lastName: 'Savic',
      address: 'Bulevar 1',
      city: 'Novi Sad',
      phoneNumber: '+381641234567',
      email: 'nikola@example.com',
      profilePhoto: null,
    });

    component.vehicleForm.patchValue({
      model: 'Car Model',
      type: 'STANDARD',
      licencePlate: 'AA-123-AA',
      seatCount: 4,
      petFriendly: false,
      babyFriendly: false,
    });

    driverServiceMock.registerDriver.and.returnValue(
      throwError(() => ({ error: { whatever: 123 } }))
    );

    component.step.set(2);
    component.createDriver();

    expect(toastMock.show).toHaveBeenCalledWith('Driver registration failed.');
    expect(component.step()).toBe(2);
    expect(component.creating()).toBeFalse();
  });
});
