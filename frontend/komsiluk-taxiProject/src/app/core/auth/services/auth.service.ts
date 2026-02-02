import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs';

export enum UserRole {
  GUEST = 'GUEST',
  PASSENGER = 'PASSENGER',
  DRIVER = 'DRIVER',
  ADMIN = 'ADMIN',
}

interface LoginResponse {
  token: string;
  id: number;
  email: string;
  role: UserRole;
  driverStatus: string | null;
}

export interface RegisterPassengerRequest {
  firstName: string;
  lastName: string;
  address: string;
  city: string;
  phoneNumber: string;
  email: string;
  password: string;
  confirmPassword: string;
  profileImageUrl: string | null;
}

export interface ForgotPasswordRequest {
  email: string;
}

export interface ResetPasswordRequest {
  token: string;
  newPassword: string;
  confirmPassword: string;
}

export interface DriverActivationRequest {
  token: string;
  password: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {

  private readonly API = 'http://localhost:8081/api';

  // Flag to prevent storage event from interfering with our own changes
  private isUpdatingAuth = false;

  private tokenSig = signal<string | null>(null);
  private roleSig = signal<UserRole>(UserRole.GUEST);
  private userIdSig = signal<number | null>(null);


  isLoggedIn = computed(() => this.tokenSig() !== null);
  userRole = computed(() => this.roleSig());
  userId = computed(() => this.userIdSig());

  constructor(private http: HttpClient) {
    this.restoreAuthState();

    // Storage event only fires from OTHER tabs, but we add a guard just in case
    window.addEventListener('storage', (event) => {
      // Skip if we're currently updating auth state ourselves
      if (this.isUpdatingAuth) {
        return;
      }

      if (event.key === 'auth_token' && event.newValue === null) {
        this.clearAuthState();
        location.href = '/login';
      }

      if (event.key === 'auth_token' && event.newValue) {
        const role = localStorage.getItem('auth_role');
        const userId = localStorage.getItem('auth_user_id');

        this.setAuthState(event.newValue, role as any, userId ? +userId : null);

        location.reload();
      }
    });
  }

  login(email: string, password: string) {
    return this.http.post<LoginResponse>(`${this.API}/auth/login`, { email, password }).pipe(
      tap(response => {
        this.setAuthState(response.token, response.role, response.id);
      })
    );
  }


  logout(): void {
    this.clearAuthState();
  }

  getToken(): string | null {
    return this.tokenSig();
  }

  private setAuthState(token: string, role: any, userId: number | null) {
    // Validate that role is a known non-GUEST role
    const validRoles = [UserRole.PASSENGER, UserRole.DRIVER, UserRole.ADMIN];
    
    if (!token || !validRoles.includes(role) || userId === null) {
      // Invalid auth state - don't save anything, clear instead
      console.warn('Invalid auth state received, clearing...', { token: !!token, role, userId });
      this.clearAuthState();
      return;
    }

    // Set flag to prevent storage event listener from interfering
    this.isUpdatingAuth = true;

    this.tokenSig.set(token);
    this.roleSig.set(role);
    this.userIdSig.set(userId);

    localStorage.setItem('auth_token', token);
    localStorage.setItem('auth_role', role);
    localStorage.setItem('auth_user_id', userId.toString());

    // Reset flag after a short delay to allow any pending storage events to be ignored
    setTimeout(() => {
      this.isUpdatingAuth = false;
    }, 100);
  }


  private clearAuthState() {
    this.isUpdatingAuth = true;
    
    this.tokenSig.set(null);
    this.roleSig.set(UserRole.GUEST);
    this.userIdSig.set(null);

    localStorage.removeItem('auth_token');
    localStorage.removeItem('auth_role');
    localStorage.removeItem('auth_user_id');
    
    setTimeout(() => {
      this.isUpdatingAuth = false;
    }, 100);
  }

  private restoreAuthState() {
    const token = localStorage.getItem('auth_token');
    const role = localStorage.getItem('auth_role') as UserRole | null;
    const userId = localStorage.getItem('auth_user_id');

    // Valid roles are only non-GUEST roles
    const validRoles = [UserRole.PASSENGER, UserRole.DRIVER, UserRole.ADMIN];

    // ALL three must exist and role must be valid (not GUEST)
    if (token && role && validRoles.includes(role) && userId) {
      this.tokenSig.set(token);
      this.roleSig.set(role);
      this.userIdSig.set(+userId);
      
      // Token will be validated on first API call via interceptor
      // If invalid, interceptor will catch 401/403 and logout
    } else {
      // Invalid or incomplete state - clear everything
      this.clearAuthState();
    }
  }

  registerPassenger(formData: FormData) {
    return this.http.post<void>(
      `${this.API}/auth/registration/passenger`,
      formData
    );
  }


  resendActivation(email: string) {
    return this.http.post<void>(
      `${this.API}/auth/registration/resend`,
      { email }
    );
  }

  activatePassenger(token: string) {
    return this.http.post<void>(
      `${this.API}/tokens/activation/passenger`,
      { token }
    );
  }

  forgotPassword(email: string) {
    return this.http.post<void>(`${this.API}/auth/forgot-password`, { email });
  }

  resetPassword(payload: ResetPasswordRequest) {
    return this.http.post<void>(`${this.API}/tokens/reset-password`, payload);
  }

  activateDriver(token: string, password: string) {
    const payload: DriverActivationRequest = { token, password };
    return this.http.post<void>(`${this.API}/tokens/activation`, payload);
  }
}
