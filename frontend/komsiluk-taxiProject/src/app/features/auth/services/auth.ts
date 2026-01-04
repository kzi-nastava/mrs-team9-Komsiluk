// feature/auth/services/auth.service.ts

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

@Injectable({ providedIn: 'root' })
export class AuthService {

  private readonly API = '/api/auth';


  private token = signal<string | null>(null);
  private role = signal<UserRole>(UserRole.GUEST);


  isLoggedIn = computed(() => this.token() !== null);
  userRole = computed(() => this.role());

  constructor(private http: HttpClient) {
    this.restoreAuthState();
  }


  login(email: string, password: string) {
    return this.http.post<LoginResponse>(`${this.API}/login`, { email, password }).pipe(
      tap(response => {
        this.setAuthState(response.token, response.role);
      })
    );
  }


  logout(): void {
    this.clearAuthState();
  }

  getToken(): string | null {
    return this.token();
  }


  private setAuthState(token: string, role: UserRole) {
    this.token.set(token);
    this.role.set(role);

    localStorage.setItem('auth_token', token);
    localStorage.setItem('auth_role', role);
  }

  private clearAuthState() {
    this.token.set(null);
    this.role.set(UserRole.GUEST);

    localStorage.removeItem('auth_token');
    localStorage.removeItem('auth_role');
  }

  private restoreAuthState() {
    const token = localStorage.getItem('auth_token');
    const role = localStorage.getItem('auth_role') as UserRole | null;

    if (token && role) {
      this.token.set(token);
      this.role.set(role);
    }
  }
}
