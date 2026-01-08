import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { FavoriteRouteCreateDTO, FavoriteRouteResponseDTO, FavoriteRouteUpdateDTO } from '../../../../../../shared/models/favorite-route.models';

@Injectable({
  providedIn: 'root',
})
export class FavoriteRouteService {
  private readonly API = 'http://localhost:8081/api';

  constructor(private http: HttpClient) {}

  getFavorites(userId: number): Observable<FavoriteRouteResponseDTO[]> {
    return this.http.get<FavoriteRouteResponseDTO[]>(`${this.API}/users/${userId}/favorites`);
  }

  addFavorite(userId: number, dto: FavoriteRouteCreateDTO): Observable<FavoriteRouteResponseDTO> {
    return this.http.post<FavoriteRouteResponseDTO>(`${this.API}/users/${userId}/favorites`, dto);
  }

   renameFavorite(favoriteId: number, dto: FavoriteRouteUpdateDTO): Observable<FavoriteRouteResponseDTO> {
    return this.http.put<FavoriteRouteResponseDTO>(`${this.API}/favorites/${favoriteId}`, dto);
  }

  deleteFavorite(favoriteId: number): Observable<void> {
    return this.http.delete<void>(`${this.API}/favorites/${favoriteId}`);
  }
}
