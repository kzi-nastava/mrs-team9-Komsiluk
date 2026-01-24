import {
  Component,
  AfterViewInit,
  effect,
  inject,
  Injector,
  runInInjectionContext,
  DestroyRef,
  ViewEncapsulation,
} from '@angular/core';
import * as L from 'leaflet';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

import { MapFacadeService } from './services/map-facade.service';
import { DriverLocationService } from './services/driver-location.service';
import { AuthService } from '../../../core/auth/services/auth.service';
import { RoutingService } from './services/routing.service';

@Component({
  selector: 'app-map',
  standalone: true,
  templateUrl: './map.html',
  styleUrls: ['./map.css'],
  encapsulation: ViewEncapsulation.None,
})
export class MapComponent implements AfterViewInit {
  private map!: L.Map;

  private injector = inject(Injector);
  private destroyRef = inject(DestroyRef);
  private facade = inject(MapFacadeService);
  private authService = inject(AuthService);

  private routing = inject(RoutingService);

  private markersLayer = L.layerGroup();
  private routeLayer: L.GeoJSON | null = null;

  private selfLayer = L.layerGroup();
  private selfMarker: L.Marker | null = null;

  // startna pozicija self markera (dok ne dobiješ realnu)
  private selfPos = L.latLng(45.2671, 19.8335);

  private animToken = 0;
  private animTimer: any = null;

  private driverNames = new Map<number, string>();

  // === DRIVERS layers (odvojeno da clearRoute ne dira vozace) ===
  private driversLayer = L.layerGroup();
  private driverMarkers = new Map<number, L.Marker>();

  // === PRE-RIDE visuals (linija koja se skraćuje + cilj marker) ===
  private preRideLine: L.Polyline | null = null;
  private preRideTargetMarker: L.Marker | null = null;

  constructor(private driverLocService: DriverLocationService) {
    effect(() => {
      const isLogged = this.authService.isLoggedIn();
      if (isLogged) {
        // sakrij druge vozace kad je ulogovan korisnik (driver)
        this.driversLayer.clearLayers();
        this.driverMarkers.clear();
      }
    });

    this.destroyRef.onDestroy(() => {
      this.stopSelfAnimation();
      this.clearPreRideVisuals();
    });
  }

  private initMap(): void {
    const noviSadBounds = L.latLngBounds([45.214, 19.764], [45.309, 19.929]);

    this.map = L.map('map', {
      center: [45.2671, 19.8335],
      zoom: 13,
      minZoom: 12,
      maxZoom: 18,
      maxBounds: noviSadBounds,
      maxBoundsViscosity: 1.0,
    });

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 18,
      attribution: '&copy; OpenStreetMap contributors',
    }).addTo(this.map);

    this.map.fitBounds(noviSadBounds);

    this.markersLayer.addTo(this.map);
    this.selfLayer.addTo(this.map);
    this.driversLayer.addTo(this.map);
  }

  private ensureSelfMarker() {
    if (this.selfMarker) return;

    // self marker (ulogovani vozac)
    this.selfMarker = L.marker(this.selfPos, { icon: this.driverFreeIcon });
    this.selfLayer.addLayer(this.selfMarker);
  }

  ngAfterViewInit(): void {
    this.initMap();
    setTimeout(() => this.map.invalidateSize(), 0);

    runInInjectionContext(this.injector, () => {
      // Standardno rutiranje (npr. za putnika / book ride) — ostaje isto
      effect(() => {
        const s = this.facade.state();
        if (!this.map) return;

        if (!s) {
          this.clearRoute();
          return;
        }
        this.renderRoute(s.points, s.geometry);
      });

      // PRE-RIDE: slušaj driveTo i vozi self marker do pickup-a
      effect(() => {
        const d = (this.facade as any).driveTo?.();
        if (!this.map) return;
        if (!d) return;

        // ako driveTo ima driverId, ignoriši ako nije naš
        const myId = Number((this.authService as any).userId?.() ?? 0);
        if (d.driverId && myId && d.driverId !== myId) return;

        const lat = d.target?.lat;
        const lon = d.target?.lon;
        if (typeof lat !== 'number' || typeof lon !== 'number') return;

        this.driveSelfTo(lat, lon);
      });
    });

    // imena vozaca (za mapu na pocetnoj)
    this.driverLocService
      .getDriversBasic()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (drivers) => {
          this.driverNames.clear();
          for (const d of drivers) {
            this.driverNames.set(d.id, `${d.firstName} ${d.lastName}`);
          }
        },
        error: (err) => {
          console.warn('getDriversBasic failed, fallback to driverId', err);
        },
      });

    // polling lokacija (samo kad nije ulogovan)
    this.driverLocService
      .pollLocations()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((locations) => {
        if (this.authService.isLoggedIn()) return;
        this.renderDrivers(locations);
      });
  }

  private clearRoute() {
    this.markersLayer.clearLayers();
    if (this.routeLayer) {
      this.map.removeLayer(this.routeLayer);
      this.routeLayer = null;
    }
  }

  // ===== PRE-RIDE HELPERS =====

  private clearPreRideVisuals() {
    if (this.preRideLine) {
      this.map.removeLayer(this.preRideLine);
      this.preRideLine = null;
    }
    if (this.preRideTargetMarker) {
      this.map.removeLayer(this.preRideTargetMarker);
      this.preRideTargetMarker = null;
    }
  }

  private stopSelfAnimation() {
    this.animToken++;
    if (this.animTimer) {
      clearTimeout(this.animTimer);
      this.animTimer = null;
    }
  }

  // Pre-ride: napravi rutu do pickup-a i animiraj marker + “pojedaj” liniju
  private driveSelfTo(lat: number, lon: number) {
    this.ensureSelfMarker();

    // stop prethodno + ocisti pre-ride
    this.stopSelfAnimation();
    this.clearPreRideVisuals();

    const from = { lat: this.selfPos.lat, lon: this.selfPos.lng, label: 'Driver' };
    const to = { lat, lon, label: 'Pickup' };

    (this.routing as any).route([from, to]).subscribe({
      next: (r: any) => {
        const geometry: GeoJSON.LineString | null = r?.geometry ?? r?.geojson ?? r;

        if (!geometry || geometry.type !== 'LineString') {
          console.warn('RoutingService returned unexpected geometry:', r);
          return;
        }

        const coords = (geometry.coordinates ?? []) as [number, number][];
        const path = coords.map(([x, y]) => L.latLng(y, x)); // [lon,lat] -> LatLng

        if (!path.length) return;

        // cilj marker (pickup) – samo ovaj marker prikazujemo, nema start ikonice
        const target = L.latLng(lat, lon);
        this.preRideTargetMarker = L.marker(target, { icon: this.stopIcon }).addTo(this.map);

        // linija (plava) koja se skraćuje
        this.preRideLine = L.polyline(path).addTo(this.map);

        const b = this.preRideLine.getBounds();
        if (b.isValid()) this.map.fitBounds(b.pad(0.2));

        // animacija
        this.animateSelfAlong(path, 600);
      },
      error: (err: any) => {
        console.warn('Could not build route to pickup', err);
      },
    });
  }

  // Animacija + skraćivanje linije (nestaje iza vozača)
  private animateSelfAlong(path: L.LatLng[], stepMs = 600) {
    if (!path.length) return;
    this.ensureSelfMarker();
    if (!this.selfMarker) return;

    const token = ++this.animToken;
    let i = 0;

    const tick = () => {
      if (token !== this.animToken) return;

      const p = path[i];
      this.selfMarker!.setLatLng(p);
      this.selfPos = p;

      // “pojedaj” liniju: ostavi samo preostali deo od trenutne tacke do kraja
      if (this.preRideLine) {
        const remaining = path.slice(i);
        this.preRideLine.setLatLngs(remaining);
      }

      i++;
      if (i < path.length) {
        this.animTimer = setTimeout(tick, stepMs);
      } else {
        this.animTimer = null;

        // stigao na pickup -> ukloni cilj marker i liniju
        this.clearPreRideVisuals();

        // opcionalno: očisti driveTo da se ne okida ponovo
        (this.facade as any).clearDriveTo?.();
      }
    };

    tick();
  }

  // ===== ICONS =====

  private startIcon = L.divIcon({
    className: 'km-pin km-pin--start',
    html: `<span class="material-symbols-outlined km-pin__icon">local_taxi</span>`,
    iconSize: [28, 28],
    iconAnchor: [14, 14],
  });

  private stopIcon = L.divIcon({
    className: 'km-pin km-pin--stop',
    html: `<span class="material-symbols-outlined km-pin__icon">location_on</span>`,
    iconSize: [28, 28],
    iconAnchor: [14, 14],
  });

  private endIcon = L.divIcon({
    className: 'km-pin km-pin--end',
    html: `<span class="material-symbols-outlined km-pin__icon">flag</span>`,
    iconSize: [30, 30],
    iconAnchor: [15, 22],
  });

  // Standardno: render rute (sa start/stop/end ikonama) — ostaje kako je bilo
  private renderRoute(
    points: { lat: number; lon: number; label?: string }[],
    geometry: GeoJSON.LineString
  ) {
    this.clearRoute();

    points.forEach((p, idx) => {
      const icon =
        idx === 0 ? this.startIcon : idx === points.length - 1 ? this.endIcon : this.stopIcon;

      const marker = L.marker([p.lat, p.lon], { icon });
      if (p.label) marker.bindPopup(p.label);
      marker.addTo(this.markersLayer);
    });

    this.routeLayer = L.geoJSON(geometry as any);
    this.routeLayer.addTo(this.map);

    const bounds = this.routeLayer.getBounds();
    if (bounds.isValid()) this.map.fitBounds(bounds.pad(0.2));
  }

  private driverFreeIcon = L.divIcon({
    className: 'km-driver km-driver--free',
    html: `<span class="material-symbols-outlined km-driver__icon">local_taxi</span>`,
    iconSize: [26, 26],
    iconAnchor: [13, 13],
  });

  private driverBusyIcon = L.divIcon({
    className: 'km-driver km-driver--busy',
    html: `<span class="material-symbols-outlined km-driver__icon">local_taxi</span>`,
    iconSize: [26, 26],
    iconAnchor: [13, 13],
  });

  // ===== DRIVERS (home map) =====

  private renderDrivers(
    locations: { driverId: number; lat: number; lng: number; busy: boolean }[]
  ): void {
    if (!this.map) return;

    const incomingIds = new Set(locations.map((l) => l.driverId));

    for (const [id, marker] of this.driverMarkers.entries()) {
      if (!incomingIds.has(id)) {
        this.driversLayer.removeLayer(marker);
        this.driverMarkers.delete(id);
      }
    }

    for (const loc of locations) {
      const name = this.driverNames.get(loc.driverId) ?? `Driver #${loc.driverId}`;
      const text = `${name} • ${loc.busy ? 'BUSY' : 'FREE'}`;

      const icon = loc.busy ? this.driverBusyIcon : this.driverFreeIcon;

      const existing = this.driverMarkers.get(loc.driverId);
      if (existing) {
        existing.setLatLng([loc.lat, loc.lng]);
        existing.setIcon(icon);
        existing.bindPopup(text);
      } else {
        const marker = L.marker([loc.lat, loc.lng], { icon });
        marker.bindPopup(text);
        this.driversLayer.addLayer(marker);

        this.driverMarkers.set(loc.driverId, marker);
      }
    }
  }
}
