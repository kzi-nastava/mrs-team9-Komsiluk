import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminDriverRegistrationSuccessPanelComponent } from './admin-driver-registration-success-panel.component';

describe('AdminDriverRegistrationSuccessPanelComponent', () => {
  let component: AdminDriverRegistrationSuccessPanelComponent;
  let fixture: ComponentFixture<AdminDriverRegistrationSuccessPanelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminDriverRegistrationSuccessPanelComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminDriverRegistrationSuccessPanelComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
