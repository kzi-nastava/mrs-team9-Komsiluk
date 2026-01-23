import { TestBed } from '@angular/core/testing';

import { DriverStartRideConfirmModalService } from './driver-start-ride-confirm-modal.service';

describe('DriverStartRideConfirmModalService', () => {
  let service: DriverStartRideConfirmModalService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DriverStartRideConfirmModalService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
