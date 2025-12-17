import { Routes } from '@angular/router';
import { LoginPage } from './pages/login/login-page.component';
import { ResetPasswordPage } from './pages/reset-password/reset-password-page.component';
import { ForgotPasswordPage } from './pages/forgot-password/forgot-password-page.component';
import { VerifyCodePage } from './pages/verify-code/verify-code-page.component';

export const AUTH_ROUTES: Routes = [
  { path: 'login', component: LoginPage },
  { path: 'forgot-password', component: ForgotPasswordPage },
  { path: 'verify-recovery-code', component: VerifyCodePage},
  { path: 'reset-password', component: ResetPasswordPage },
];