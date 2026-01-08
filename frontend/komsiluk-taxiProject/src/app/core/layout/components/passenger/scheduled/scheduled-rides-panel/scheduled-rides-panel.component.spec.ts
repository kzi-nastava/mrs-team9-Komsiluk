import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ScheduledRidesPanelComponent } from './scheduled-rides-panel.component';

describe('ScheduledRidesPanelComponent', () => {
  let component: ScheduledRidesPanelComponent;
  let fixture: ComponentFixture<ScheduledRidesPanelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ScheduledRidesPanelComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ScheduledRidesPanelComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
