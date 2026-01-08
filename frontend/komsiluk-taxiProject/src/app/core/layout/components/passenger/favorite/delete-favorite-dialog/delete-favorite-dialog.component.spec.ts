import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DeleteFavoriteDialogComponent } from './delete-favorite-dialog.component';

describe('DeleteFavoriteDialogComponent', () => {
  let component: DeleteFavoriteDialogComponent;
  let fixture: ComponentFixture<DeleteFavoriteDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DeleteFavoriteDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DeleteFavoriteDialogComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
