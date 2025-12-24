import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { ResetPasswordComponent } from './pages/reset-password/reset-password.component';
import { ForgotPasswordComponent } from './pages/forgot-password/forgot-password.component';
import { ActivationMessageComponent } from './pages/activation-message/activation-message.component';
import { RiderRegistrationComponent } from './pages/rider-registration/rider-registration.component';
import { SuccessfulRegistrationComponent } from './pages/successful-registration/successful-registration.component';

export const AUTH_ROUTES: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'forgot-password', component: ForgotPasswordComponent },
  { path: 'reset-password', component: ResetPasswordComponent },
  { path: 'activation-message', component: ActivationMessageComponent },
  { path: 'register-rider', component: RiderRegistrationComponent },
  { path: 'successful-registration', component: SuccessfulRegistrationComponent }
];