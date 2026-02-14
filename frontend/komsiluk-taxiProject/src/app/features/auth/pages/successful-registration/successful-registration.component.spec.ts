import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { SuccessfulRegistrationComponent } from './successful-registration.component';

describe('SuccessfulRegistrationComponent', () => {
  let component: SuccessfulRegistrationComponent;
  let fixture: ComponentFixture<SuccessfulRegistrationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SuccessfulRegistrationComponent],
      providers: [provideRouter([])]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SuccessfulRegistrationComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
