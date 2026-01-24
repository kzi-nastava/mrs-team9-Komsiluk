import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DriverStartRideConfirmDialogComponent } from './driver-start-ride-confirm-dialog.component';

describe('DriverStartRideConfirmDialogComponent', () => {
  let component: DriverStartRideConfirmDialogComponent;
  let fixture: ComponentFixture<DriverStartRideConfirmDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DriverStartRideConfirmDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DriverStartRideConfirmDialogComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
