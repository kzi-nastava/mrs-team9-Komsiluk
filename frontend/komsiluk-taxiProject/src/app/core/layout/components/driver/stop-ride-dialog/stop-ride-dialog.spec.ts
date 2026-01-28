import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StopRideDialog } from './stop-ride-dialog';

describe('StopRideDialog', () => {
  let component: StopRideDialog;
  let fixture: ComponentFixture<StopRideDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StopRideDialog]
    })
    .compileComponents();

    fixture = TestBed.createComponent(StopRideDialog);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
