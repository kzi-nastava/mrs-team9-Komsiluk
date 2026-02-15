import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivationComponent } from './activation.component';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../../../core/auth/services/auth.service';
import { ToastService } from '../../../../shared/components/toast/toast.service';
import { of, throwError } from 'rxjs';

describe('ActivationComponent', () => {
  let component: ActivationComponent;
  let fixture: ComponentFixture<ActivationComponent>;
  
  let mockRouter: jasmine.SpyObj<Router>;
  let mockAuthService: jasmine.SpyObj<AuthService>;
  let mockToastService: jasmine.SpyObj<ToastService>;
  let mockActivatedRoute: any;

  beforeEach(async () => {
    mockRouter = jasmine.createSpyObj('Router', ['navigate']);
    mockAuthService = jasmine.createSpyObj('AuthService', ['activatePassenger']);
    mockToastService = jasmine.createSpyObj('ToastService', ['show']);
    
    mockActivatedRoute = {
      snapshot: {
        queryParamMap: {
          get: jasmine.createSpy('get')
        }
      }
    };

    await TestBed.configureTestingModule({
      imports: [ ActivationComponent ], 
    })
    .overrideComponent(ActivationComponent, {
      set: {
        providers: [
          { provide: Router, useValue: mockRouter },
          { provide: AuthService, useValue: mockAuthService },
          { provide: ToastService, useValue: mockToastService },
          { provide: ActivatedRoute, useValue: mockActivatedRoute }
        ]
      }
    })
    .compileComponents();

    fixture = TestBed.createComponent(ActivationComponent);
    component = fixture.componentInstance;
  });

  it('should show error and navigate back if token is missing', () => {
    mockActivatedRoute.snapshot.queryParamMap.get.and.returnValue(null);

    fixture.detectChanges();

    expect(mockToastService.show).toHaveBeenCalledWith('Invalid activation link.');
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/activation-message']);
    expect(mockAuthService.activatePassenger).not.toHaveBeenCalled();
  });

  it('should navigate to success page on successful activation', () => {
    const testToken = 'valid-token';
    mockActivatedRoute.snapshot.queryParamMap.get.and.returnValue(testToken);
    mockAuthService.activatePassenger.and.returnValue(of(undefined));

    fixture.detectChanges();

    expect(mockAuthService.activatePassenger).toHaveBeenCalledWith(testToken);
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/successful-registration']);
  });

  it('should show error toast and navigate back on activation failure', () => {
    const testToken = 'expired-token';
    mockActivatedRoute.snapshot.queryParamMap.get.and.returnValue(testToken);
    mockAuthService.activatePassenger.and.returnValue(throwError(() => ({ status: 400 })));

    fixture.detectChanges();

    expect(mockToastService.show).toHaveBeenCalledWith('Activation link is invalid or expired.');
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/activation-message']);
  });
});