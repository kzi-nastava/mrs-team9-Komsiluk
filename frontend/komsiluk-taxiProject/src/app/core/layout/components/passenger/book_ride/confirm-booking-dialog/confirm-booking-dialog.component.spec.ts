import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConfirmBookingDialogComponent } from './confirm-booking-dialog.component';

describe('ConfirmBookingDialogComponent', () => {
  let component: ConfirmBookingDialogComponent;
  let fixture: ComponentFixture<ConfirmBookingDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConfirmBookingDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ConfirmBookingDialogComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
