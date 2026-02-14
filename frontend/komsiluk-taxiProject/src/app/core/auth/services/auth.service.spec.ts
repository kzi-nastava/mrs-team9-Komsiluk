import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { AuthService } from './auth.service';

describe('AuthService (activateDriver)', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  const API = 'http://localhost:8081/api';
  const URL = `${API}/tokens/activation`;

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
});
