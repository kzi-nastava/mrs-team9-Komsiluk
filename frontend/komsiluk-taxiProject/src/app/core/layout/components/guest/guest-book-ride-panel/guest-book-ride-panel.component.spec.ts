import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GuestBookRidePanelComponent } from './guest-book-ride-panel.component';

describe('GuestBookRidePanelComponent', () => {
  let component: GuestBookRidePanelComponent;
  let fixture: ComponentFixture<GuestBookRidePanelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GuestBookRidePanelComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GuestBookRidePanelComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
