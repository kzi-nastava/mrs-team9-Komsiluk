import { Component, AfterViewInit, effect, inject, Injector, runInInjectionContext, DestroyRef, ViewEncapsulation } from '@angular/core';
import * as L from 'leaflet';
import { MapFacadeService } from './services/map-facade.service';
import { DriverLocationService } from './services/driver-location.service';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { AuthService } from '../../../core/auth/services/auth.service';

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

  private markersLayer = L.layerGroup();
  private routeLayer: L.GeoJSON | null = null;

  private driverNames = new Map<number, string>();


  // === DRIVERS layers (novo, odvojeno da clearRoute ne dira vozace) ===
  private driversLayer = L.layerGroup();
  private driverMarkers = new Map<number, L.Marker>();


  constructor(private driverLocService: DriverLocationService) {
    effect(() => {
      const isLogged = this.authService.isLoggedIn();
      if (isLogged) {
        console.log('Korisnik ulogovan - uklanjam vozače sa mape');
        this.driversLayer.clearLayers();
        this.driverMarkers.clear();
      }
    });
  }

  private initMap(): void {
    const noviSadBounds = L.latLngBounds(
      [45.214, 19.764], // SW
      [45.309, 19.929]  // NE
    );

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
    this.driversLayer.addTo(this.map);
  }

  ngAfterViewInit(): void {
    this.initMap();
    setTimeout(() => this.map.invalidateSize(), 0);

    runInInjectionContext(this.injector, () => {
      effect(() => {
        const s = this.facade.state();
        if (!this.map) return;

        if (!s) {
          this.clearRoute();
          return;
        }
        this.renderRoute(s.points, s.geometry);
      });
    });


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
        }
      });


    this.driverLocService
      .pollLocations()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((locations) => {
        // <--- KORAK 3: Provera u pretplati
        if (this.authService.isLoggedIn()) {
          return; 
        }
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

  private renderRoute(points: {lat:number; lon:number; label?:string}[], geometry: GeoJSON.LineString) {
    this.clearRoute();

    points.forEach((p, idx) => {
      const icon =
        idx === 0 ? this.startIcon :
        idx === points.length - 1 ? this.endIcon :
        this.stopIcon;

      const marker = L.marker([p.lat, p.lon], { icon });
      if (p.label) marker.bindPopup(p.label);
      marker.addTo(this.markersLayer);
    });

    this.routeLayer = L.geoJSON(geometry as any);
    this.routeLayer.addTo(this.map);

    const bounds = this.routeLayer.getBounds();
    if (bounds.isValid()) this.map.fitBounds(bounds.pad(0.2));
  }

  // (opciono) lepši ikonice za busy/free
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

  private renderDrivers(
    locations: { driverId: number; lat: number; lng: number; busy: boolean }[]
  ): void {
    if (!this.map) return;

    const incomingIds = new Set(locations.map(l => l.driverId));

    // remove markere kojih vise nema
    for (const [id, marker] of this.driverMarkers.entries()) {
      if (!incomingIds.has(id)) {
        this.driversLayer.removeLayer(marker);
        this.driverMarkers.delete(id);
      }
    }

    // upsert markeri
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
