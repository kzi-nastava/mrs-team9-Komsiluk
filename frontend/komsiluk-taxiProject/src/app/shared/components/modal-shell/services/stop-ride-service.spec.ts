import { TestBed } from '@angular/core/testing';

import { StopRideService } from './stop-ride-service';

describe('StopRideService', () => {
  let service: StopRideService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(StopRideService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
