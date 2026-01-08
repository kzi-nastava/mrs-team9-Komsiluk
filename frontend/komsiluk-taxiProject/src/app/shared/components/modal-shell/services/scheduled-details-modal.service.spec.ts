import { TestBed } from '@angular/core/testing';

import { ScheduledDetailsModalService } from './scheduled-details-modal.service';

describe('ScheduledDetailsModalService', () => {
  let service: ScheduledDetailsModalService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ScheduledDetailsModalService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
