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


@Injectable({ providedIn: 'root' })
export class AuthService {

  private readonly API = 'http://localhost:8081/api/auth';


  private tokenSig = signal<string | null>(null);
  private roleSig = signal<UserRole>(UserRole.GUEST);
  private userIdSig = signal<number | null>(null);


  isLoggedIn = computed(() => this.tokenSig() !== null);
  userRole = computed(() => this.roleSig());
  userId = computed(() => this.userIdSig());

  constructor(private http: HttpClient) {
    this.restoreAuthState();
  }

  login(email: string, password: string) {
    return this.http.post<LoginResponse>(`${this.API}/login`, { email, password }).pipe(
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

  private setAuthState(token: string, role: UserRole, userId: number) {
    this.tokenSig.set(token);
    this.roleSig.set(role);
    this.userIdSig.set(userId);

    localStorage.setItem('auth_token', token);
    localStorage.setItem('auth_role', role);
    localStorage.setItem('auth_user_id', userId.toString());
  }

  private clearAuthState() {
    this.tokenSig.set(null);
    this.roleSig.set(UserRole.GUEST);
    this.userIdSig.set(null);

    localStorage.removeItem('auth_token');
    localStorage.removeItem('auth_role');
    localStorage.removeItem('auth_user_id');
  }

  private restoreAuthState() {
    const token = localStorage.getItem('auth_token');
    const role = localStorage.getItem('auth_role') as UserRole | null;
    const userId = localStorage.getItem('auth_user_id');

    if (token && role) {
      this.tokenSig.set(token);
      this.roleSig.set(role);
      this.userIdSig.set(userId ? +userId : null);
    }
  }

  registerPassenger(payload: RegisterPassengerRequest) {
    return this.http.post<void>(
      `${this.API}/registration/passenger`,
      payload
    );
  }

  resendActivation(email: string) {
    return this.http.post<void>(
      `${this.API}/registration/resend`,
      { email }
    );
  }


}
