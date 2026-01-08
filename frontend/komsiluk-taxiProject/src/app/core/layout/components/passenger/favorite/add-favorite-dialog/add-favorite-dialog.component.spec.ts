import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddFavoriteDialogComponent } from './add-favorite-dialog.component';

describe('AddFavoriteDialogComponent', () => {
  let component: AddFavoriteDialogComponent;
  let fixture: ComponentFixture<AddFavoriteDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddFavoriteDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddFavoriteDialogComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
