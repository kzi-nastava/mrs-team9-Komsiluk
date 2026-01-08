import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RenameFavoriteDialogComponent } from './rename-favorite-dialog.component';

describe('RenameFavoriteDialogComponent', () => {
  let component: RenameFavoriteDialogComponent;
  let fixture: ComponentFixture<RenameFavoriteDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RenameFavoriteDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RenameFavoriteDialogComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
