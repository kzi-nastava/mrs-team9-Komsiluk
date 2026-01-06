import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PassengerRegistrationComponent } from './passenger-registration.component';

describe('PassengerRegistrationComponent', () => {
  let component: PassengerRegistrationComponent;
  let fixture: ComponentFixture<PassengerRegistrationComponent>;
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PassengerRegistrationComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PassengerRegistrationComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
