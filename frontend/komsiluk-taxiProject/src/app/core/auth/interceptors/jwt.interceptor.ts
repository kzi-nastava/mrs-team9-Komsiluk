import { inject } from '@angular/core';
import {
  HttpInterceptorFn,
  HttpRequest,
  HttpHandlerFn,
  HttpEvent,
  HttpErrorResponse
} from '@angular/common/http';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';

export const jwtInterceptor: HttpInterceptorFn = (
  req: HttpRequest<unknown>,
  next: HttpHandlerFn
) => {

  const AUTH_IGNORED_ROUTES = [
    '/api/auth/login',
    '/api/auth/registration',
    '/api/auth/forgot-password',
    '/api/auth/reset-password'
  ];

  if (AUTH_IGNORED_ROUTES.some(path => req.url.includes(path))) {
    return next(req);
  }

  const BACKEND_BASES = [
    'http://localhost:8081',
    'https://localhost:8081'
  ];

  const isBackendRequest = BACKEND_BASES.some(base => req.url.startsWith(base));

  if (!isBackendRequest) {
    return next(req);
  }

  const auth = inject(AuthService);
  const router = inject(Router);

  const token = auth.getToken();
  
  const authReq = token
    ? req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`,
      },
    })
    : req;

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      console.error('HTTP Error in interceptor:', error.status, error.url);

      if (error.status === 401) {
        console.warn('401 Unauthorized - logging out');
        auth.logout();
        router.navigate(['/login']);
      }

      if (error.status === 403) {
        // Token might be invalid or user doesn't have permission
        // Only logout if it's an auth-related 403, not a permission issue
        const token = auth.getToken();
        if (token) {
          // Check if token is expired or invalid
          try {
            const payload = JSON.parse(atob(token.split('.')[1]));
            const exp = payload.exp * 1000; // Convert to milliseconds
            if (Date.now() > exp) {
              auth.logout();
              router.navigate(['/login']);
            }
          } catch {
            // Invalid token format, logout
            auth.logout();
            router.navigate(['/login']);
          }
        }
      }

      return throwError(() => error);
    })
  );
};
