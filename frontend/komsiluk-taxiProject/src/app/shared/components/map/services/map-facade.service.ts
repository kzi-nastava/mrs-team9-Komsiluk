import { Injectable, signal, computed } from '@angular/core';

export interface GeoPoint {
  lat: number;
  lon: number;
  label?: string;
}

export interface RouteRenderState {
  points: GeoPoint[];
  geometry: GeoJSON.LineString;
}

@Injectable({ providedIn: 'root' })
export class MapFacadeService {
  private stateSig = signal<RouteRenderState | null>(null);
  state = computed(() => this.stateSig());

  setState(s: RouteRenderState | null) {
    this.stateSig.set(s);
  }
}
