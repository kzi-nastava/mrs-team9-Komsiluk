import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FavoriteDetailsDialogComponent } from './favorite-details-dialog.component';

describe('FavoriteDetailsDialogComponent', () => {
  let component: FavoriteDetailsDialogComponent;
  let fixture: ComponentFixture<FavoriteDetailsDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FavoriteDetailsDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FavoriteDetailsDialogComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
