import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PassengerBookRidePanelComponent } from './passenger-book-ride-panel.component';

describe('PassengerBookRidePanelComponent', () => {
  let component: PassengerBookRidePanelComponent;
  let fixture: ComponentFixture<PassengerBookRidePanelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PassengerBookRidePanelComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PassengerBookRidePanelComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
