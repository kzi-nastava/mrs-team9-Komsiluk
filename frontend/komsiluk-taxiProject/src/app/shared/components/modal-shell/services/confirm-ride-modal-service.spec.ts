import { TestBed } from '@angular/core/testing';

import { ConfirmRideModalService } from './confirm-ride-modal-service';

describe('ConfirmRideModalService', () => {
  let service: ConfirmRideModalService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ConfirmRideModalService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
