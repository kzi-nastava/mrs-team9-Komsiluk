import { TestBed } from '@angular/core/testing';

import { ScheduledRideService } from './scheduled-ride.service';

describe('ScheduledRideService', () => {
  let service: ScheduledRideService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ScheduledRideService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
