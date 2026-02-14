import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CancelRideDialogComponent } from './cancel-ride-dialog.component';

describe('CancelRideDialogComponent', () => {
  let component: CancelRideDialogComponent;
  let fixture: ComponentFixture<CancelRideDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CancelRideDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CancelRideDialogComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
