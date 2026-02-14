import { TestBed } from '@angular/core/testing';

import { LeftSidebarCommandService } from './left-sidebar-command-service.service';

describe('LeftSidebarCommandService', () => {
  let service: LeftSidebarCommandService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(LeftSidebarCommandService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
