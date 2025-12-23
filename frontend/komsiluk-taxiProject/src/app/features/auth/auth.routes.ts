import { Routes } from '@angular/router';
import { LoginPage } from './pages/login/login.component';
import { ResetPasswordPage } from './pages/reset-password/reset-password-page.component';
import { ForgotPasswordPage } from './pages/forgot-password/forgot-password.component';
import { ActivationMessage } from './pages/activation-message/activation-message.component';

export const AUTH_ROUTES: Routes = [
  { path: 'login', component: LoginPage },
  { path: 'forgot-password', component: ForgotPasswordPage },
  { path: 'reset-password', component: ResetPasswordPage },
  { path: 'activation-message', component: ActivationMessage },
];