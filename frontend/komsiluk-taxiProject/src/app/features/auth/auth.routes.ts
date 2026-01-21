import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { ResetPasswordComponent } from './pages/reset-password/reset-password.component';
import { ForgotPasswordComponent } from './pages/forgot-password/forgot-password.component';
import { ActivationMessageComponent } from './pages/activation-message/activation-message.component';
import { PassengerRegistrationComponent } from './pages/passenger-registration/passenger-registration.component';
import { SuccessfulRegistrationComponent } from './pages/successful-registration/successful-registration.component';
import { ActivationComponent } from './components/activation/activation.component';
import { ForgotPasswordMessageComponent } from './pages/forgot-password-message/forgot-password-message.component';
import { GuestGuard } from '../../core/auth/guards/guest.guard';
import { DriverActivationPageComponent } from './pages/driver-activation-page/driver-activation-page.component';

export const AUTH_ROUTES: Routes = [
  { path: 'login', component: LoginComponent, canActivate: [GuestGuard] },
  { path: 'forgot-password', component: ForgotPasswordComponent , canActivate: [GuestGuard] },
  { path: 'reset-password', component: ResetPasswordComponent},
  { path: 'activation-message', component: ActivationMessageComponent },
  { path: 'register-passenger', component: PassengerRegistrationComponent, canActivate: [GuestGuard] },
  { path: 'successful-registration', component: SuccessfulRegistrationComponent },
  { path: 'forgot-password-message', component: ForgotPasswordMessageComponent},
  { path: 'activation', component: ActivationComponent },
  { path: 'driver-activation', component: DriverActivationPageComponent }
];