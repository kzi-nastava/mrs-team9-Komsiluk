import { TestBed } from '@angular/core/testing';

import { RideProgressService } from './ride-progress-service';

describe('RideProgressService', () => {
  let service: RideProgressService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RideProgressService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
