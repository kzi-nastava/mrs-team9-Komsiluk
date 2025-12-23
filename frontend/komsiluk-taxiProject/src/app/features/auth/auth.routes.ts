import { Routes } from '@angular/router';
import { LoginPage } from './pages/login/login-page.component';
import { ResetPasswordPage } from './pages/reset-password/reset-password-page.component';
import { ForgotPasswordPage } from './pages/forgot-password/forgot-password-page.component';
import { RecoveryActivation } from './pages/recovery-activation/recovery-activation';

export const AUTH_ROUTES: Routes = [
  { path: 'login', component: LoginPage },
  { path: 'forgot-password', component: ForgotPasswordPage },
  { path: 'reset-password', component: ResetPasswordPage },
  { path: 'recovery-activation', component: RecoveryActivation },
];