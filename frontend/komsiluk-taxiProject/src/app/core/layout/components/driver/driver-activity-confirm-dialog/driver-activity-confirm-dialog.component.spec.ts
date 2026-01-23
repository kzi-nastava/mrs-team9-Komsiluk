import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DriverActivityConfirmDialogComponent } from './driver-activity-confirm-dialog.component';

describe('DriverActivityConfirmDialogComponent', () => {
  let component: DriverActivityConfirmDialogComponent;
  let fixture: ComponentFixture<DriverActivityConfirmDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DriverActivityConfirmDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DriverActivityConfirmDialogComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
