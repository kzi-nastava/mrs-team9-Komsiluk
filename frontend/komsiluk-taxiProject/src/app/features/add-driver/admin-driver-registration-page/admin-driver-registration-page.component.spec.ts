import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminDriverRegistrationPageComponent } from './admin-driver-registration-page.component';

describe('AdminDriverRegistrationPageComponent', () => {
  let component: AdminDriverRegistrationPageComponent;
  let fixture: ComponentFixture<AdminDriverRegistrationPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminDriverRegistrationPageComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminDriverRegistrationPageComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
