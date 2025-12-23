// feature/auth/services/auth.service.ts

import { Injectable } from '@angular/core';
import { signal } from '@angular/core';



export enum UserRole {
  GUEST = 'GUEST',
  PASSENGER = 'PASSENGER',
  DRIVER = 'DRIVER',
  ADMIN = 'ADMIN',
}

interface FakeUser {
  email: string;
  password: string;
  role: UserRole;
}

const FAKE_USERS: FakeUser[] = [
  {
    email: 'passenger@test.com',
    password: 'pass12345',
    role: UserRole.PASSENGER,
  },
  {
    email: 'driver@test.com',
    password: 'driver12345',
    role: UserRole.DRIVER,
  },
  {
    email: 'admin@test.com',
    password: 'admin12345',
    role: UserRole.ADMIN,
  },
];


@Injectable({ providedIn: 'root' })
export class AuthService {
  role = signal<UserRole>(UserRole.GUEST);

  login(email: string, password: string): boolean {
    const user = FAKE_USERS.find(
      u => u.email === email && u.password === password
    );

    if (!user) {
      return false;
    }

    this.role.set(user.role);
    return true;
  }

  logout(): void {
    this.role.set(UserRole.GUEST);
  }

  getRole(): UserRole {
    return this.role();
  }

  isLoggedIn(): boolean {
    return this.role() !== UserRole.GUEST;
  }
}
