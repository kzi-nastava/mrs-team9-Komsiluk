import { TestBed } from '@angular/core/testing';

import { BookRidePrefillService } from './book-ride-prefill.service';

describe('BookRidePrefillService', () => {
  let service: BookRidePrefillService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BookRidePrefillService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
