import { TestBed } from '@angular/core/testing';

import { FavoriteDetailsModalService } from './favorite-details-modal.service';

describe('FavoriteDetailsModalService', () => {
  let service: FavoriteDetailsModalService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FavoriteDetailsModalService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
