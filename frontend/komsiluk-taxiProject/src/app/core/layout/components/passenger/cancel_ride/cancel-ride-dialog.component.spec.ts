import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PassengerCancelRideComponent } from './cancel-ride-dialog.component';

describe('PassengerCancelRideComponent', () => {
  let component: PassengerCancelRideComponent;
  let fixture: ComponentFixture<PassengerCancelRideComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PassengerCancelRideComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PassengerCancelRideComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
