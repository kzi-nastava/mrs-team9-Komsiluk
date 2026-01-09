import { TestBed } from '@angular/core/testing';

import { BlockNoteService } from './block-note.service';

describe('BlockNoteService', () => {
  let service: BlockNoteService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BlockNoteService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
