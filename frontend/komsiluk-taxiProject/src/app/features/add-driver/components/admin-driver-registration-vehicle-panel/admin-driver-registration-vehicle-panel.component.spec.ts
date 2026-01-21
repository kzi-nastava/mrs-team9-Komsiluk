import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminDriverRegistrationVehiclePanelComponent } from './admin-driver-registration-vehicle-panel.component';

describe('AdminDriverRegistrationVehiclePanelComponent', () => {
  let component: AdminDriverRegistrationVehiclePanelComponent;
  let fixture: ComponentFixture<AdminDriverRegistrationVehiclePanelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminDriverRegistrationVehiclePanelComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminDriverRegistrationVehiclePanelComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
