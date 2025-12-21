import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DriverRideHistoryPageComponent } from './driver-ride-history-page.component';

describe('DriverRideHistoryPage', () => {
  let component: DriverRideHistoryPageComponent;
  let fixture: ComponentFixture<DriverRideHistoryPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DriverRideHistoryPageComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DriverRideHistoryPageComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
