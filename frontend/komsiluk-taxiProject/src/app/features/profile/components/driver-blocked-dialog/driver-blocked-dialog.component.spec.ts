import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DriverBlockedDialogComponent } from './driver-blocked-dialog.component';

describe('DriverBlockedDialogComponent', () => {
  let component: DriverBlockedDialogComponent;
  let fixture: ComponentFixture<DriverBlockedDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DriverBlockedDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DriverBlockedDialogComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
