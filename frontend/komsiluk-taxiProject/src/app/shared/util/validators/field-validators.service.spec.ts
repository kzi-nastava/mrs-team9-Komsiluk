import { TestBed } from '@angular/core/testing';

import { FieldValidatorsService } from './field-validators.service';

describe('FieldValidatorsService', () => {
  let service: FieldValidatorsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FieldValidatorsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
