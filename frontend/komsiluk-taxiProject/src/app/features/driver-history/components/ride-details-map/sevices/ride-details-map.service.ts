import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class MapService {
  constructor(private http: HttpClient) {}

  search(street: string): Observable<any> {
    return this.http.get(
      `https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(street)}`
    );
  }
}