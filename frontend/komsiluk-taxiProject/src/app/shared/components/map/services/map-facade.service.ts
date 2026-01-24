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

  private driveToSig = signal<{driverId: number, target: {lat: number, lon: number}} | null>(null);
  driveTo = computed(() => this.driveToSig());

  setState(s: RouteRenderState | null) {
    this.stateSig.set(s);
  }

  setDriveTo(driverId: number, lat: number, lon: number) {
    console.log(`[FACADE] Primljene koordinate za vozača ${driverId}:`, lat, lon);
    this.driveToSig.set({ driverId, target: { lat, lon } });
  }
  clearDriveTo() {
    this.driveToSig.set(null);
  }
}
