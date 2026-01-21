import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DriverActivationPageComponent } from './driver-activation-page.component';

describe('DriverActivationPageComponent', () => {
  let component: DriverActivationPageComponent;
  let fixture: ComponentFixture<DriverActivationPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DriverActivationPageComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DriverActivationPageComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
