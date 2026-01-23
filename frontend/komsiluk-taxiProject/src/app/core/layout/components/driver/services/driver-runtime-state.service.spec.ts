import { TestBed } from '@angular/core/testing';

import { DriverRuntimeStateService } from './driver-runtime-state.service';

describe('DriverRuntimeStateService', () => {
  let service: DriverRuntimeStateService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DriverRuntimeStateService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
