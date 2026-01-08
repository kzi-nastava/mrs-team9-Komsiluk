import { TestBed } from '@angular/core/testing';

import { RidePlannerService } from './ride-planner.service';

describe('RidePlannerService', () => {
  let service: RidePlannerService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RidePlannerService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
