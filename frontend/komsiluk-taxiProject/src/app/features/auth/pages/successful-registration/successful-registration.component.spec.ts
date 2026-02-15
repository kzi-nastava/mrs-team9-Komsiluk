import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, RouterLink } from '@angular/router';
import { By } from '@angular/platform-browser';
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
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the success message', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('div')?.textContent)
      .toContain('Your account has been successfully activated!');
  });

  it('should have a working link to login page', () => {
    const buttonDebugElement = fixture.debugElement.query(By.css('button'));
    const routerLinkInstance = buttonDebugElement.injector.get(RouterLink);

    expect(routerLinkInstance.urlTree!.toString()).toBe('/login');
  });
});