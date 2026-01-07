import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map } from 'rxjs';

export interface GeoPoint { lat: number; lon: number; label?: string; }

export interface RouteResult {
  distanceMeters: number;
  durationSeconds: number;
  geometry: GeoJSON.LineString;
}

@Injectable({ providedIn: 'root' })
export class RoutingService {
  constructor(private http: HttpClient) {}

  route(points: GeoPoint[]) {
    const coords = points.map(p => `${p.lon},${p.lat}`).join(';');
    const url =
      `https://router.project-osrm.org/route/v1/driving/${coords}` +
      `?overview=full&geometries=geojson&steps=false`;

    return this.http.get<any>(url).pipe(
      map(res => {
        const r = res.routes?.[0];
        return {
          distanceMeters: r.distance,
          durationSeconds: r.duration,
          geometry: r.geometry as GeoJSON.LineString,
        } as RouteResult;
      })
    );
  }
}
