import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule, FormControl, FormGroup } from '@angular/forms';
import { AdminDriverRegistrationVehiclePanelComponent } from './admin-driver-registration-vehicle-panel.component';

describe('AdminDriverRegistrationVehiclePanelComponent', () => {
  let component: AdminDriverRegistrationVehiclePanelComponent;
  let fixture: ComponentFixture<AdminDriverRegistrationVehiclePanelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminDriverRegistrationVehiclePanelComponent, ReactiveFormsModule],
    }).compileComponents();

    fixture = TestBed.createComponent(AdminDriverRegistrationVehiclePanelComponent);
    component = fixture.componentInstance;

    (component as any).form = new FormGroup({
      model: new FormControl(''),
      type: new FormControl(''),
      licencePlate: new FormControl(''),
      seatCount: new FormControl(''),
      petFriendly: new FormControl(false),
      babyFriendly: new FormControl(false),
    });

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
