import { TestBed } from '@angular/core/testing';

import { ScheduledRidesService } from './scheduled-rides-service';

describe('ScheduledRidesService', () => {
  let service: ScheduledRidesService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ScheduledRidesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
