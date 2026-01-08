import { TestBed } from '@angular/core/testing';

import { FavoritesBusService } from './favorites-bus.service';

describe('FavoritesBusService', () => {
  let service: FavoritesBusService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FavoritesBusService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
