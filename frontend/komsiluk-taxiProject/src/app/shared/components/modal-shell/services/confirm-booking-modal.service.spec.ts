import { TestBed } from '@angular/core/testing';

import { ConfirmBookingModalService } from './confirm-booking-modal.service';

describe('ConfirmBookingModalService', () => {
  let service: ConfirmBookingModalService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ConfirmBookingModalService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
