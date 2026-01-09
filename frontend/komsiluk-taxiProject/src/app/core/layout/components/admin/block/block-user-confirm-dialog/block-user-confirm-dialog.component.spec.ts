import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BlockUserConfirmDialogComponent } from './block-user-confirm-dialog.component';

describe('BlockUserConfirmDialogComponent', () => {
  let component: BlockUserConfirmDialogComponent;
  let fixture: ComponentFixture<BlockUserConfirmDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BlockUserConfirmDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BlockUserConfirmDialogComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
