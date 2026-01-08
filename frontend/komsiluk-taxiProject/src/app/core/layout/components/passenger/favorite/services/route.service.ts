import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { RouteCreateDTO, RouteResponseDTO } from '../../../../../../shared/models/route.models';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class RouteService {
  private readonly API = 'http://localhost:8081/api/routes';

  constructor(private http: HttpClient) {}

  findOrCreate(dto: RouteCreateDTO): Observable<RouteResponseDTO> {
    return this.http.post<RouteResponseDTO>(`${this.API}/find-or-create`, dto);
  }
}
