import { TestBed } from '@angular/core/testing';

import { CancelRideModalService } from './confirm-ride-modal-service';

describe('ConfirmRideModalService', () => {
  let service: CancelRideModalService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CancelRideModalService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
