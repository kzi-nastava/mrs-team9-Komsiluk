import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminDriverChangeRequestsPageComponent } from './admin-driver-change-requests-page.component';

describe('AdminDriverChangeRequestsPageComponent', () => {
  let component: AdminDriverChangeRequestsPageComponent;
  let fixture: ComponentFixture<AdminDriverChangeRequestsPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminDriverChangeRequestsPageComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminDriverChangeRequestsPageComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
