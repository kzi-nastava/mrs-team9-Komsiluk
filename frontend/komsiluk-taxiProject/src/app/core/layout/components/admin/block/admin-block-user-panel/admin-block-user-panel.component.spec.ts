import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminBlockUserPanelComponent } from './admin-block-user-panel.component';

describe('AdminBlockUserPanelComponent', () => {
  let component: AdminBlockUserPanelComponent;
  let fixture: ComponentFixture<AdminBlockUserPanelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminBlockUserPanelComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminBlockUserPanelComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
