import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RideHistoryCardComponent } from './ride-history-card.component.component';

describe('RideHistoryCardComponent', () => {
  let component: RideHistoryCardComponent;
  let fixture: ComponentFixture<RideHistoryCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RideHistoryCardComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(RideHistoryCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
