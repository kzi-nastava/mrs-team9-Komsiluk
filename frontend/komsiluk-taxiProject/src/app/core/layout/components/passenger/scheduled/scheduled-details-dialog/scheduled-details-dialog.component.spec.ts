import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ScheduledDetailsDialogComponent } from './scheduled-details-dialog.component';

describe('ScheduledDetailsDialogComponent', () => {
  let component: ScheduledDetailsDialogComponent;
  let fixture: ComponentFixture<ScheduledDetailsDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ScheduledDetailsDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ScheduledDetailsDialogComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
