import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AdminDriverRegistrationDriverPanelComponent } from './admin-driver-registration-driver-panel.component';
import { FormControl, FormGroup } from '@angular/forms';

describe('AdminDriverRegistrationDriverPanelComponent', () => {
  let component: AdminDriverRegistrationDriverPanelComponent;
  let fixture: ComponentFixture<AdminDriverRegistrationDriverPanelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminDriverRegistrationDriverPanelComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminDriverRegistrationDriverPanelComponent);
    component = fixture.componentInstance;

    (component as any).form = new FormGroup({
      firstName: new FormControl(''),
      lastName: new FormControl(''),
      email: new FormControl(''),
      address: new FormControl(''),
      phoneNumber: new FormControl(''),
      city: new FormControl(''),
      profilePhoto: new FormControl(null)
    });

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
