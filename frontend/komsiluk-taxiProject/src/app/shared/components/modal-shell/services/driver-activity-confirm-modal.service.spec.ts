import { TestBed } from '@angular/core/testing';

import { DriverActivityConfirmModalService } from './driver-activity-confirm-modal.service';

describe('DriverActivityConfirmModalService', () => {
  let service: DriverActivityConfirmModalService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DriverActivityConfirmModalService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
