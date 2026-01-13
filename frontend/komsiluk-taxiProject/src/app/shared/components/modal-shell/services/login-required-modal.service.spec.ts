import { TestBed } from '@angular/core/testing';

import { LoginRequiredModalService } from './login-required-modal.service';

describe('LoginRequiredModalService', () => {
  let service: LoginRequiredModalService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(LoginRequiredModalService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
