import { TestBed } from '@angular/core/testing';

import { BlockUserConfirmModalService } from './block-user-confirm-modal.service';

describe('BlockUserConfirmModalService', () => {
  let service: BlockUserConfirmModalService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BlockUserConfirmModalService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
