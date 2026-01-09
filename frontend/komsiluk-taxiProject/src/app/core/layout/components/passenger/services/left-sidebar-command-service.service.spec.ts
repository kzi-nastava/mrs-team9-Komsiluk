import { TestBed } from '@angular/core/testing';

import { LeftSidebarCommandServiceService } from './left-sidebar-command-service.service';

describe('LeftSidebarCommandServiceService', () => {
  let service: LeftSidebarCommandServiceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(LeftSidebarCommandServiceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
