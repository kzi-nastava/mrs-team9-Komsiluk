import { TestBed } from '@angular/core/testing';

import { RenameFavoriteModalService } from './rename-favorite-modal.service';

describe('RenameFavoriteModalService', () => {
  let service: RenameFavoriteModalService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RenameFavoriteModalService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
