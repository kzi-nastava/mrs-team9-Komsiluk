import { TestBed } from '@angular/core/testing';

import { PanicModalService } from './panic-modal-service';

describe('PanicModalService', () => {
  let service: PanicModalService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PanicModalService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
