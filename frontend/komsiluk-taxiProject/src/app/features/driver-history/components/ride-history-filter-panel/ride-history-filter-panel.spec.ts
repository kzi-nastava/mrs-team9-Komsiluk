import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RideHistoryFilterPanelComponent } from './ride-history-filter-panel';

describe('RideHistoryFilterPanel', () => {
  let component: RideHistoryFilterPanelComponent;
  let fixture: ComponentFixture<RideHistoryFilterPanelComponent>;
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RideHistoryFilterPanelComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RideHistoryFilterPanelComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
