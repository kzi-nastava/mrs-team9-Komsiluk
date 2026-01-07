import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { ResetPasswordComponent } from './pages/reset-password/reset-password.component';
import { ForgotPasswordComponent } from './pages/forgot-password/forgot-password.component';
import { ActivationMessageComponent } from './pages/activation-message/activation-message.component';
import { PassengerRegistrationComponent } from './pages/passenger-registration/passenger-registration.component';
import { SuccessfulRegistrationComponent } from './pages/successful-registration/successful-registration.component';
import { ActivationComponent } from './components/activation/activation.component';
import { GuestGuard } from '../../core/auth/guards/guest.guard';

export const AUTH_ROUTES: Routes = [
  { path: 'login', component: LoginComponent, canActivate: [GuestGuard] },
  { path: 'forgot-password', component: ForgotPasswordComponent , canActivate: [GuestGuard] },
  { path: 'reset-password', component: ResetPasswordComponent, canActivate: [GuestGuard] },
  { path: 'activation-message', component: ActivationMessageComponent },
  { path: 'register-passenger', component: PassengerRegistrationComponent, canActivate: [GuestGuard] },
  { path: 'successful-registration', component: SuccessfulRegistrationComponent },
  { path: 'activation', component: ActivationComponent }
];