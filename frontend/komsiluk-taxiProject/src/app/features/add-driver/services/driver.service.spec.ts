import { TestBed } from '@angular/core/testing';
import {HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';

import { DriverService } from './driver.service';
import { DriverCreateDTO } from '../../../shared/models/driver.models';

describe('DriverService', () => {
  let service: DriverService;
  let httpMock: HttpTestingController;

  const API = 'http://localhost:8081/api/drivers';

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [],
      providers: [DriverService, provideHttpClientTesting()],
    });

    service = TestBed.inject(DriverService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('registerDriver should POST FormData with JSON blob and WITHOUT profileImage when file is null', () => {

    const dto: DriverCreateDTO = {
      firstName: 'Driver',
      lastName: 'Test',
      address: 'Address 123',
      city: 'City',
      phoneNumber: '12345678',
      email: 'email@example.com',
      profileImageUrl: null,
      vehicle: {
        model: 'Car Model',
        type: 'STANDARD',
        licencePlate: 'AA-123-AA',
        seatCount: 4,
        babyFriendly: false,
        petFriendly: true,
      },
    };

    service.registerDriver(dto, null).subscribe();

    const req = httpMock.expectOne(API);
    expect(req.request.method).toBe('POST');

    const body = req.request.body as FormData;
    expect(body instanceof FormData).toBeTrue();

    expect(body.has('data')).toBeTrue();
    expect(body.has('profileImage')).toBeFalse();

    req.flush({
      id: 1,
      email: dto.email,
      firstName: dto.firstName,
      lastName: dto.lastName,
      address: dto.address,
      city: dto.city,
      phoneNumber: dto.phoneNumber,
      profileImageUrl: '',
      active: false,
      blocked: false,
      createdAt: '',
      role: 'DRIVER',
      driverStatus: 'INACTIVE',
      vehicle: {},
    });
  });

  it('registerDriver should POST FormData with JSON blob and WITH profileImage when file is provided', async () => {

    const dto: DriverCreateDTO = {
      firstName: 'Driver',
      lastName: 'Test',
      address: 'Address 123',
      city: 'City',
      phoneNumber: '12345678',
      email: 'email@example.com',
      profileImageUrl: null,
      vehicle: {
        model: 'Car Model',
        type: 'STANDARD',
        licencePlate: 'AA-123-AA',
        seatCount: 4,
        babyFriendly: true,
        petFriendly: false,
      },
    };

    const file = new File(['x'], 'avatar.png', { type: 'image/png' });

    service.registerDriver(dto, file).subscribe();

    const req = httpMock.expectOne(API);
    expect(req.request.method).toBe('POST');

    const body = req.request.body as FormData;
    expect(body instanceof FormData).toBeTrue();

    expect(body.has('data')).toBeTrue();
    expect(body.has('profileImage')).toBeTrue();

    const img = body.get('profileImage');
    expect(img).toBeTruthy();
    expect((img as File).name).toBe('avatar.png');

    req.flush({
      id: 1,
      email: dto.email,
      firstName: dto.firstName,
      lastName: dto.lastName,
      address: dto.address,
      city: dto.city,
      phoneNumber: dto.phoneNumber,
      profileImageUrl: '',
      active: false,
      blocked: false,
      createdAt: '',
      role: 'DRIVER',
      driverStatus: 'INACTIVE',
      vehicle: {},
    });
  });
});
