import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RiderRegistrationComponent } from './rider-registration.component';

describe('RiderRegistrationComponent', () => {
  let component: RiderRegistrationComponent;
  let fixture: ComponentFixture<RiderRegistrationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RiderRegistrationComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RiderRegistrationComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
