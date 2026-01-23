import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DriverCurrentRidePanelComponent } from './driver-current-ride-panel.component';

describe('DriverCurrentRidePanelComponent', () => {
  let component: DriverCurrentRidePanelComponent;
  let fixture: ComponentFixture<DriverCurrentRidePanelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DriverCurrentRidePanelComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DriverCurrentRidePanelComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
