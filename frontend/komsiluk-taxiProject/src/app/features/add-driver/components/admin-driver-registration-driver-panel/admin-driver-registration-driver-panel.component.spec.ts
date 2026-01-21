import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminDriverRegistrationDriverPanelComponent } from './admin-driver-registration-driver-panel.component';

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
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
