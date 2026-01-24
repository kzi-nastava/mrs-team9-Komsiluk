import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DriverSidebarComponent } from './driver-left-menu.component';

describe('DriverSidebarComponent', () => {
  let component: DriverSidebarComponent;
  let fixture: ComponentFixture<DriverSidebarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DriverSidebarComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DriverSidebarComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should toggle current ride open state', () => {
    expect(component.currentRideOpen()).toBeFalsy();
    component.toggle('currentRide');
    expect(component.currentRideOpen()).toBeTruthy();
    component.toggle('currentRide');
    expect(component.currentRideOpen()).toBeFalsy();
  });

  it('should toggle scheduled rides open state', () => {
    expect(component.scheduledRidesOpen()).toBeFalsy();
    component.toggle('scheduledRides');
    expect(component.scheduledRidesOpen()).toBeTruthy();
    component.toggle('scheduledRides');
    expect(component.scheduledRidesOpen()).toBeFalsy();
  });
});
