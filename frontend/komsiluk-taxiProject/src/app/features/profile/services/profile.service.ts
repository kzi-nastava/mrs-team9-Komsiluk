import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../../../core/auth/services/auth.service';
import { UserProfileResponseDTO, UserProfileUpdateDTO, DriverEditRequestCreateDTO, DriverEditRequestResponseDTO } from '../../../shared/models/profile.models';

@Injectable({ providedIn: 'root' })
export class ProfileService {
  private readonly API = 'http://localhost:8081/api/users';
  private readonly DRIVER_EDIT_API = 'http://localhost:8081/api/driver-edit-requests';

  constructor(private http: HttpClient, private auth: AuthService) {}

  getMyProfile(): Observable<UserProfileResponseDTO> {
    const id = this.auth.userId();

    return this.http.get<UserProfileResponseDTO>(`${this.API}/${id}/profile`);
  }

  changeMyPassword(oldPassword: string, newPassword: string) {
    const id = this.auth.userId();

    const body = { oldPassword, newPassword };
    return this.http.put<void>(`${this.API}/${id}/password`, body);
  }

  updateProfile(dto: UserProfileUpdateDTO) {
    const id = this.auth.userId();

    return this.http.put<any>(`${this.API}/${id}/profile`, dto);
  }

  createDriverEditRequest(dto: DriverEditRequestCreateDTO): Observable<DriverEditRequestResponseDTO> {
    const driverId = this.auth.userId();

    return this.http.post<DriverEditRequestResponseDTO>(`${this.DRIVER_EDIT_API}/${driverId}`, dto);
  }
}
