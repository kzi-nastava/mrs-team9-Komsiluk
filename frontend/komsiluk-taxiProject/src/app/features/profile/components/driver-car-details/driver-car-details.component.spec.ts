import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DriverCarDetailsComponent } from './driver-car-details.component';

describe('DriverCarDetailsComponent', () => {
  let component: DriverCarDetailsComponent;
  let fixture: ComponentFixture<DriverCarDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DriverCarDetailsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DriverCarDetailsComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
