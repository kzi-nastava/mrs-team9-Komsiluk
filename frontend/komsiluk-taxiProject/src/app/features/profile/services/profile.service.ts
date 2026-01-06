import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../../../core/auth/services/auth.service';
import { UserProfileResponseDTO } from '../../../shared/models/profile.models';

@Injectable({ providedIn: 'root' })
export class ProfileService {
  private readonly API = 'http://localhost:8081/api/users';

  constructor(private http: HttpClient, private auth: AuthService) {}

  getMyProfile(): Observable<UserProfileResponseDTO> {
    const id = this.auth.userId();

    return this.http.get<UserProfileResponseDTO>(`${this.API}/${id}/profile`);
  }
}
