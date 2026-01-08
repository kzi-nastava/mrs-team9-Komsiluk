import { TestBed } from '@angular/core/testing';

import { AddFavoriteModalService } from './add-favorite-modal.service';

describe('AddFavoriteModalService', () => {
  let service: AddFavoriteModalService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AddFavoriteModalService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
