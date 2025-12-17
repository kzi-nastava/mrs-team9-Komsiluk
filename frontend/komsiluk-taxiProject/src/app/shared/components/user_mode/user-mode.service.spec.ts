import { TestBed } from '@angular/core/testing';

import { UserModeService } from './user-mode.service';

describe('UserModeService', () => {
  let service: UserModeService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(UserModeService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
