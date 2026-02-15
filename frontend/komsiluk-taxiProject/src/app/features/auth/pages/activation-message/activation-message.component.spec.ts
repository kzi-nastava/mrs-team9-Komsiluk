import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivationMessageComponent } from './activation-message.component';
import { ActivatedRoute } from '@angular/router';
import { AuthService } from '../../../../core/auth/services/auth.service';
import { ToastService } from '../../../../shared/components/toast/toast.service';
import { of, throwError } from 'rxjs';

describe('ActivationMessageComponent', () => {
  let component: ActivationMessageComponent;
  let fixture: ComponentFixture<ActivationMessageComponent>;
  
  // Mock objekti
  let authServiceMock: jasmine.SpyObj<AuthService>;
  let toastServiceMock: jasmine.SpyObj<ToastService>;
  let activatedRouteMock: any;

  beforeEach(async () => {
    authServiceMock = jasmine.createSpyObj('AuthService', ['resendActivation']);
    toastServiceMock = jasmine.createSpyObj('ToastService', ['show']);
    
    activatedRouteMock = {
      snapshot: {
        queryParamMap: {
          get: jasmine.createSpy('get').and.returnValue('test@example.com')
        }
      }
    };

    await TestBed.configureTestingModule({
      imports: [ActivationMessageComponent],
      providers: [
        { provide: AuthService, useValue: authServiceMock },
        { provide: ToastService, useValue: toastServiceMock },
        { provide: ActivatedRoute, useValue: activatedRouteMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ActivationMessageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create and extract email from query params', () => {
    expect(component).toBeTruthy();
    expect(component.email).toBe('test@example.com');
    expect(activatedRouteMock.snapshot.queryParamMap.get).toHaveBeenCalledWith('email');
  });

  it('resendActivationEmail should call auth service and show success toast', () => {
    authServiceMock.resendActivation.and.returnValue(of(undefined));

    component.resendActivationEmail();

    expect(component.loading).toBeFalse();
    expect(authServiceMock.resendActivation).toHaveBeenCalledWith('test@example.com');
    expect(toastServiceMock.show).toHaveBeenCalledWith('If the email exists, the activation link was sent.');
  });

  it('resendActivationEmail should handle 429 error (Too Many Requests)', () => {
    authServiceMock.resendActivation.and.returnValue(throwError(() => ({ status: 429 })));

    component.resendActivationEmail();

    expect(toastServiceMock.show).toHaveBeenCalledWith('Activation email already sent. Please check your inbox.');
    expect(component.loading).toBeFalse();
  });

  it('resendActivationEmail should handle 400 error (Bad Request)', () => {
    authServiceMock.resendActivation.and.returnValue(throwError(() => ({ status: 400 })));

    component.resendActivationEmail();

    expect(toastServiceMock.show).toHaveBeenCalledWith('Invalid email address.');
    expect(component.loading).toBeFalse();
  });

  it('resendActivationEmail should handle generic errors', () => {
    authServiceMock.resendActivation.and.returnValue(throwError(() => ({ status: 500 })));

    component.resendActivationEmail();

    expect(toastServiceMock.show).toHaveBeenCalledWith('Something went wrong. Please try again later.');
    expect(component.loading).toBeFalse();
  });

  it('resendActivationEmail should not call service if email is missing', () => {
    component.email = null;
    
    component.resendActivationEmail();

    expect(authServiceMock.resendActivation).not.toHaveBeenCalled();
  });

  it('resendActivationEmail should not call service if already loading', () => {
    component.loading = true;
    authServiceMock.resendActivation.and.returnValue(of(undefined));

    component.resendActivationEmail();

    expect(authServiceMock.resendActivation).not.toHaveBeenCalled();
  });
});