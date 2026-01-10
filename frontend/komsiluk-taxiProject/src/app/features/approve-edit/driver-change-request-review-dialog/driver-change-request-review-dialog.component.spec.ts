import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DriverChangeRequestReviewDialogComponent } from './driver-change-request-review-dialog.component';

describe('DriverChangeRequestReviewDialogComponent', () => {
  let component: DriverChangeRequestReviewDialogComponent;
  let fixture: ComponentFixture<DriverChangeRequestReviewDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DriverChangeRequestReviewDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DriverChangeRequestReviewDialogComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
