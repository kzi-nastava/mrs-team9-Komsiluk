import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DriverCreateDTO, DriverResponseDTO } from '../../../shared/models/driver.models';

@Injectable({ providedIn: 'root' })
export class DriverService {
  private readonly API = 'http://localhost:8081/api/drivers';

  constructor(private http: HttpClient) {}

  registerDriver(dto: DriverCreateDTO, profileImage?: File | null): Observable<DriverResponseDTO> {
    const fd = new FormData();
    fd.append('data', new Blob([JSON.stringify(dto)], { type: 'application/json' }));

    if (profileImage) {
      fd.append('profileImage', profileImage, profileImage.name);
    }

    return this.http.post<DriverResponseDTO>(this.API, fd);
  }
}
