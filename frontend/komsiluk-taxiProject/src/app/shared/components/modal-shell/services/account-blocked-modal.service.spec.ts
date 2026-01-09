import { TestBed } from '@angular/core/testing';

import { AccountBlockedModalService } from './account-blocked-modal.service';

describe('AccountBlockedModalService', () => {
  let service: AccountBlockedModalService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AccountBlockedModalService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
