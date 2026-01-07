import { TestBed } from '@angular/core/testing';

import { MapFacadeService } from './map-facade.service';

describe('MapFacadeService', () => {
  let service: MapFacadeService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MapFacadeService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
