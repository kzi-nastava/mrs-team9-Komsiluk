import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FavoriteRidesPanelComponent } from './favorite-rides-panel.component';

describe('FavoriteRidesPanelComponent', () => {
  let component: FavoriteRidesPanelComponent;
  let fixture: ComponentFixture<FavoriteRidesPanelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FavoriteRidesPanelComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FavoriteRidesPanelComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
