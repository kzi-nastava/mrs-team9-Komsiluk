import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { AuthService } from './auth.service';

describe('AuthService (activateDriver)', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  const API = 'http://localhost:8081/api';
  const URL = `${API}/tokens/activation`;
  const AUTH_URL = `${API}/auth`;

  beforeEach(() => {
    spyOn(localStorage, 'getItem').and.returnValue(null);

    TestBed.configureTestingModule({
      providers: [
        AuthService,
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('activateDriver should POST correct payload to /tokens/activation', () => {
    const token = 'ABC';
    const password = 'Password1';

    service.activateDriver(token, password).subscribe();

    const req = httpMock.expectOne({ url: URL, method: 'POST' });

    expect(req.request.body).toEqual({ token: 'ABC', password: 'Password1' });

    req.flush(null);
  });

  it('activateDriver should propagate HTTP error', () => {
    const token = 'ABC';
    const password = 'Password1';

    let receivedError: any = null;

    service.activateDriver(token, password).subscribe({
      next: () => fail('Expected error, got success'),
      error: (err) => (receivedError = err),
    });

    const req = httpMock.expectOne({ url: URL, method: 'POST' });

    req.flush({ message: 'Expired' }, { status: 400, statusText: 'Bad Request' });

    expect(receivedError).toBeTruthy();
    expect(receivedError.status).toBe(400);
  });

  /**
   * Passenger registration
   */

  it('registerPassenger should POST form data to /auth/registration/passenger', () => {
    const payload = {
      firstName: "Passenger",
      lastName: "Test",
      address: "Address 123",
      city: "City",
      phoneNumber: "12345678",
      email: "test@example.com",
      password: "Password123",
      confirmPassword: "Password123"
    };

    const formData = new FormData();
    formData.append('data', new Blob([JSON.stringify(payload)], { type: 'application/json' }));

    formData.append('profileImage', new File([], 'profile.png'));


    service.registerPassenger(formData).subscribe();

    const req = httpMock.expectOne({ url: `${AUTH_URL}/registration/passenger`, method: 'POST' });

    expect(req.request.body).toEqual(formData);

    req.flush(null);
  });

  it('registerPassenger should POST form data without profileImage when file is not provided', () => {
    const payload = {
      firstName: "Passenger",
      lastName: "Test",
      address: "Address 123",
      city: "City",
      phoneNumber: "12345678",
      email: "test@example.com",
      password: "Password123",
      confirmPassword: "Password123"
    };

    const formData = new FormData();
    formData.append('data', new Blob([JSON.stringify(payload)], { type: 'application/json' }));


    service.registerPassenger(formData).subscribe();

    const req = httpMock.expectOne({ url: `${AUTH_URL}/registration/passenger`, method: 'POST' });

    expect(req.request.body).toEqual(formData);

    req.flush(null);
  });

  it('registerPassenger should propagate HTTP error', () => {
    const payload = {
      firstName: "Passenger",
      lastName: "Test",
      address: "Address 123",
      city: "City",
      phoneNumber: "12345678",
      email: "test@example.com",
      password: "Password123",
      confirmPassword: "Password123"
    };

    const formData = new FormData();
    formData.append('data', new Blob([JSON.stringify(payload)], { type: 'application/json' }));


    service.registerPassenger(formData).subscribe({
      next: () => fail('Expected error, got success'),
      error: (err) => {
        expect(err.status).toBe(409);
      }
    });

    const req = httpMock.expectOne({ url: `${AUTH_URL}/registration/passenger`, method: 'POST' });

    req.flush({ message: 'Email already exists' }, { status: 409, statusText: 'Conflict' });
  });

  it('resendActivation should POST email to /auth/registration/resend', () => {
    const email = 'passenger@test.com';

    service.resendActivation(email).subscribe();

    const req = httpMock.expectOne({ url: `${AUTH_URL}/registration/resend`, method: 'POST' });

    expect(req.request.body).toEqual({ email });

    req.flush(null);
  });

  it('resendActivation should propagate HTTP error', () => {
    const email = 'passenger@test.com';

    let receivedError: any = null;

    service.resendActivation(email).subscribe({
      next: () => fail('Expected error, got success'),
      error: (err) => (receivedError = err),
    });

    const req = httpMock.expectOne({ url: `${AUTH_URL}/registration/resend`, method: 'POST' });

    req.flush({ message: 'Email not found' }, { status: 404, statusText: 'Not Found' });

    expect(receivedError).toBeTruthy();
    expect(receivedError.status).toBe(404);
  });

  it('activatePassenger should POST token to /tokens/activation/passenger', () => {
    const token = 'ACTIVATION_TOKEN';

    service.activatePassenger(token).subscribe();

    const req = httpMock.expectOne({ url: `${API}/tokens/activation/passenger`, method: 'POST' });

    expect(req.request.body).toEqual({ token });

    req.flush(null);
  });

  it('activatePassenger should propagate HTTP error', () => {
    const token = 'ACTIVATION_TOKEN';

    let receivedError: any = null;
    service.activatePassenger(token).subscribe({
      next: () => fail('Expected error, got success'),
      error: (err) => (receivedError = err),
    });
    const req = httpMock.expectOne({ url: `${API}/tokens/activation/passenger`, method: 'POST' });

    req.flush({ message: 'Invalid or expired token' }, { status: 400, statusText: 'Bad Request' });

    expect(receivedError).toBeTruthy();
    expect(receivedError.status).toBe(400);
  });
});