import { TestBed } from '@angular/core/testing';

import { DeleteFavoriteModalService } from './delete-favorite-modal.service';

describe('DeleteFavoriteModalService', () => {
  let service: DeleteFavoriteModalService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DeleteFavoriteModalService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
