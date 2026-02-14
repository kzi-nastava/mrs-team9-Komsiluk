import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { PassengerLeftMenuComponent } from './passenger-left-menu.component';

describe('PassengerLeftMenuComponent', () => {
  let component: PassengerLeftMenuComponent;
  let fixture: ComponentFixture<PassengerLeftMenuComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PassengerLeftMenuComponent],
      providers: [provideRouter([])]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PassengerLeftMenuComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
