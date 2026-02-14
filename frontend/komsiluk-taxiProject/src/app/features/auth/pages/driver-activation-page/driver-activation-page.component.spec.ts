import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router, ActivatedRoute, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, throwError } from 'rxjs';

import { DriverActivationPageComponent } from './driver-activation-page.component';
import { AuthService } from '../../../../core/auth/services/auth.service';
import { ToastService } from '../../../../shared/components/toast/toast.service';

describe('DriverActivationPageComponent', () => {
  let fixture: ComponentFixture<DriverActivationPageComponent>;
  let component: DriverActivationPageComponent;

  let authMock: jasmine.SpyObj<AuthService>;
  let toastMock: jasmine.SpyObj<ToastService>;
  let router: Router;

  let tokenFromUrl: string | null = 'VALID_TOKEN';

  beforeEach(async () => {
    authMock = jasmine.createSpyObj<AuthService>('AuthService', ['activateDriver']);
    toastMock = jasmine.createSpyObj<ToastService>('ToastService', ['show']);

    await TestBed.configureTestingModule({
      imports: [DriverActivationPageComponent, RouterTestingModule],
      providers: [
        { provide: AuthService, useValue: authMock },
        { provide: ToastService, useValue: toastMock },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              queryParamMap: {
                get: (key: string) => (key === 'token' ? tokenFromUrl : null),
              },
            },
          },
        },
      ],
    }).compileComponents();

    router = TestBed.inject(Router);
    spyOn(router, 'navigate').and.resolveTo(true);

    fixture = TestBed.createComponent(DriverActivationPageComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('ngOnInit should read token from URL when present and stay on step 1', () => {
    tokenFromUrl = 'ABC';
    fixture.detectChanges();

    expect(component.token).toBe('ABC');
    expect(component.step()).toBe(1);
    expect(toastMock.show).not.toHaveBeenCalled();
  });

  it('ngOnInit should show toast and navigate home when token is missing', () => {
    tokenFromUrl = null;

    fixture.detectChanges();

    expect(component.token).toBeNull();
    expect(toastMock.show).toHaveBeenCalledWith('Invalid activation link.');
    expect(router.navigate).toHaveBeenCalledWith(['']);
  });

  it('mismatch should return true when password and repeat differ', () => {
    tokenFromUrl = 'ABC';
    fixture.detectChanges();

    component.form.patchValue({
      password: 'Password1',
      repeat: 'Password2',
    });

    expect(component.mismatch()).toBeTrue();
  });

  it('submit should NOT call auth.activateDriver when form is invalid', () => {
    tokenFromUrl = 'ABC';
    fixture.detectChanges();

    component.form.reset({ password: '', repeat: '' });

    component.submit();

    expect(authMock.activateDriver).not.toHaveBeenCalled();
  });

  it('submit should NOT call auth.activateDriver when passwords mismatch', () => {
    tokenFromUrl = 'ABC';
    fixture.detectChanges();

    component.form.patchValue({
      password: 'Password1',
      repeat: 'Password2',
    });

    component.submit();

    expect(authMock.activateDriver).not.toHaveBeenCalled();
  });

  it('submit should call auth.activateDriver(token, password) and go to step 2 on success', () => {
    tokenFromUrl = 'ABC';
    authMock.activateDriver.and.returnValue(of(void 0));

    fixture.detectChanges();

    component.form.patchValue({
      password: 'Password1',
      repeat: 'Password1',
    });

    component.submit();

    expect(authMock.activateDriver).toHaveBeenCalledTimes(1);
    expect(authMock.activateDriver).toHaveBeenCalledWith('ABC', 'Password1');

    expect(component.step()).toBe(2);
    expect(component.loading()).toBeFalse();

    expect(router.navigate).toHaveBeenCalledWith([], {
      queryParams: { token: null },
      queryParamsHandling: 'merge',
      replaceUrl: true,
    });
  });

  it('submit should show toast and navigate home on error', () => {
    tokenFromUrl = 'ABC';
    authMock.activateDriver.and.returnValue(
      throwError(() => ({ error: { message: 'Activation link is invalid or expired.' } }))
    );

    fixture.detectChanges();

    component.form.patchValue({
      password: 'Password1',
      repeat: 'Password1',
    });

    component.submit();

    expect(toastMock.show).toHaveBeenCalledWith('Activation link is invalid or expired.');
    expect(router.navigate).toHaveBeenCalledWith(['']);
    expect(component.loading()).toBeFalse();
    expect(component.step()).toBe(1);
  });

  it('submit should use default error message when backend does not provide message', () => {
    tokenFromUrl = 'ABC';
    authMock.activateDriver.and.returnValue(
      throwError(() => ({ error: {} }))
    );

    fixture.detectChanges();

    component.form.patchValue({
      password: 'Password1',
      repeat: 'Password1',
    });

    component.submit();

    expect(toastMock.show).toHaveBeenCalledWith('Activation link is invalid or expired.');
    expect(router.navigate).toHaveBeenCalledWith(['']);
  });

it('should show success UI after successful activation', () => {
    tokenFromUrl = 'ABC';
    authMock.activateDriver.and.returnValue(of(void 0));

    fixture.detectChanges();

    component.form.patchValue({ password: 'Password1', repeat: 'Password1' });
    component.submit();

    fixture.detectChanges();

    const el: HTMLElement = fixture.nativeElement;
    const text = el.textContent || '';

    expect(text).toContain('Your driver account has been successfully activated!');

    const buttons = Array.from(el.querySelectorAll('button'));
    const goLoginBtn = buttons.find(b => (b.textContent || '').includes('Go to login'));
    expect(goLoginBtn).toBeTruthy();
  });
});
