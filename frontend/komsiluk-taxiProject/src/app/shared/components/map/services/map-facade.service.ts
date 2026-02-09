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
export type RideWaypoint = { lat: number; lon: number; label?: string };

@Injectable({ providedIn: 'root' })
export class MapFacadeService {
    private resetMapSig = signal(false);
    resetMap = computed(() => this.resetMapSig());

    triggerResetMap() {
      this.resetMapSig.set(true);
      setTimeout(() => this.resetMapSig.set(false), 0);
    }
  private stateSig = signal<RouteRenderState | null>(null);
  state = computed(() => this.stateSig());

  private driveToSig = signal<{driverId: number, target: {lat: number, lon: number}} | null>(null);
  driveTo = computed(() => this.driveToSig());

  ridePath = signal<RideWaypoint[] | null>(null);

  activeDriverId = signal<number | null>(null);
  activeRideMarkers = signal<RideWaypoint[] | null>(null);

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
  setRidePath(points: RideWaypoint[]) {
    this.ridePath.set(points);
  }

  clearRidePath() {
    this.ridePath.set(null);
  }

  // --- NOVE METODE KOJE POZIVAMO IZ KOMPONENTE ---
  setFocusRide(driverId: number, waypoints: RideWaypoint[]) {
    this.activeDriverId.set(driverId);
    this.activeRideMarkers.set(waypoints);
  }

  clearFocusRide() {
    this.activeDriverId.set(null);
    this.activeRideMarkers.set(null);
  }
}
