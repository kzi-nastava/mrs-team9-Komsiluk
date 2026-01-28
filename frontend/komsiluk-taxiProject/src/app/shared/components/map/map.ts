import {Component,AfterViewInit,effect,inject,Injector,runInInjectionContext,DestroyRef,ViewEncapsulation,} from '@angular/core';
import * as L from 'leaflet';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { MapFacadeService } from './services/map-facade.service';
import { DriverLocationService } from './services/driver-location.service';
import { AuthService } from '../../../core/auth/services/auth.service';
import { RoutingService } from './services/routing.service';
import { RideService } from '../../../core/layout/components/passenger/book_ride/services/ride.service';
import { GeocodingService } from './services/geocoding.service';
import { interval, switchMap } from 'rxjs';

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

  private activePassengerMarkers = L.layerGroup();
  private targetDriverId: number | null = null;

  private selfPos = L.latLng(45.2671, 19.8335);

  private animToken = 0;
  private animTimer: any = null;

  private driversLayer = L.layerGroup();
  private driverMarkers = new Map<number, L.Marker>();

  private preRideLine: L.Polyline | null = null;
  private preRideTargetMarker: L.Marker | null = null;

  private liveRideLine: L.Polyline | null = null;
  private liveRideTargetMarker: L.Marker | null = null;

  private rideService = inject(RideService);
private geocodingService = inject(GeocodingService);

  private lastLocationPushAt = 0;
  private locationPushEveryMs = 1000;

  constructor(private driverLocService: DriverLocationService) {
    effect(() => {
      const isLogged = this.authService.isLoggedIn();
      if (isLogged) {
        this.driversLayer.clearLayers();
        this.driverMarkers.clear();
      }else {
      this.driversLayer.clearLayers();
      this.driverMarkers.clear();
      this.selfMarker = null;
      this.targetDriverId = null;

      this.clearRoute();

      this.clearLiveRideVisuals();
      
      this.activePassengerMarkers.clearLayers();

      this.stopSelfAnimation();
      this.clearPreRideVisuals();
      
      this.facade.activeDriverId.set(null);
      if ((this.facade as any).clearDriveTo) {
        (this.facade as any).clearDriveTo();
      }
    
    }
    });

    this.destroyRef.onDestroy(() => {
      this.stopSelfAnimation();
      this.clearPreRideVisuals();
      this.clearLiveRideVisuals();
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
  const myId = Number((this.authService as any).userId?.() ?? 0);
  if (!myId) return;

  let marker = this.driverMarkers.get(myId);

  if (!marker) {
    marker = L.marker(this.selfPos, { icon: this.driverFreeIcon });
    this.driversLayer.addLayer(marker);
    this.driverMarkers.set(myId, marker);
  }
  
  this.selfMarker = marker;
}
ngAfterViewInit(): void {
  this.initMap();
  setTimeout(() => this.map.invalidateSize(), 0);

  runInInjectionContext(this.injector, () => {
    //Search route
    effect(() => {
      const s = this.facade.state();
      if (!this.map) return;
      
      // If there is no state, clear route
      if (!s) {
        this.clearRoute();
        return;
      }
      
      // When there is a target driver (active ride), do not render search route
      if (!this.targetDriverId) {
        this.renderRoute(s.points, s.geometry);
      }
    });

    //Driver (Drive to pickup)
    effect(() => {
      const d = (this.facade as any).driveTo?.();
      if (!this.map || !d) return;
      const myId = Number((this.authService as any).userId?.() ?? 0);
      if (d.driverId && myId && d.driverId !== myId) return;
      this.driveSelfTo(d.target.lat, d.target.lon);
    });

    //Synchronization of active driver
    effect(() => {
      const pts = (this.facade as any).ridePath?.();
      const activeDriverId = (this.facade as any).activeDriverId?.(); 
      const role = this.authService.userRole();
      if (role === 'DRIVER') {
      // Start animation if user is driver
      this.startLiveRide(pts);
    } 
    else if (role === 'PASSENGER' && activeDriverId) {
      // If user is passenger set ID so renderDrivers shows the vehicle
      this.targetDriverId = activeDriverId;
    }
    });
  });

  // POLLING LOCATIONS (For displaying drivers on the map)
  this.driverLocService.pollLocations()
    .pipe(takeUntilDestroyed(this.destroyRef))
    .subscribe((locations) => {
      this.renderDrivers(locations);
    });

  // Polling for active ride (Transition from search to ride)
  
  if (this.authService.userRole() === 'PASSENGER') {
    interval(500)
      .pipe(
        switchMap(() => this.rideService.getActiveRideForPassenger()),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe((ride) => {
        if (ride && ride.driverId) {
          this.facade.activeDriverId.set(ride.driverId);
          this.targetDriverId = ride.driverId;
         
          //Delete previous visuals
          this.clearRoute();

          //Geocoding and drawing active route
          const addressQueries = [ride.startAddress, ...ride.stops, ride.endAddress];
          
          import('rxjs').then(({ forkJoin }) => {
            forkJoin(addressQueries.map(addr => this.geocodingService.lookupOne(addr)))
              .subscribe(results => {
                const validPoints = results.filter(res => res !== null) as any[];
                if (validPoints.length >= 2) {
                  // Draw route from active ride
                  this.calculatePassengerPath(validPoints);
                }
              });
          });
        }
        else {
          // Ride is finished (or does not exist)
          if (this.targetDriverId !== null) { // Check if the ride was just active
            this.handleRideFinish();
          }
        }
      });
      
  }
}

private handleRideFinish() {
  this.targetDriverId = null;
  this.facade.activeDriverId.set(null);
  
  this.clearLiveRideVisuals();
  this.activePassengerMarkers.clearLayers();
  
  // If user is passenger, clear all driver markers from the map
  if (this.authService.userRole() === 'PASSENGER') {
    this.driversLayer.clearLayers();
    this.driverMarkers.clear();
  }
  // If user is driver, don't clear own marker
}
private calculatePassengerPath(points: any[]) {
  if (this.liveRideLine) return;

  (this.routing as any).route(points).subscribe({
    next: (r: any) => {
      const geometry = r?.geometry ?? r?.geojson ?? r;
      if (!geometry || geometry.type !== 'LineString') return;

      const coords = (geometry.coordinates ?? []) as [number, number][];
      const path = coords.map(([x, y]) => L.latLng(y, x));

      this.facade.setRidePath(path.map(p => ({ lat: p.lat, lon: p.lng })));

      this.clearLiveRideVisuals();
      this.liveRideLine = L.polyline(path, { color: 'blue', weight: 5 }).addTo(this.map);
      this.renderPassengerActiveRide(points);
    }
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

  private driveSelfTo(lat: number, lon: number) {
    this.ensureSelfMarker();

    if (this.selfMarker) {
    this.selfPos = this.selfMarker.getLatLng();
  }

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

        if (path.length < 2) return;

        const target = L.latLng(lat, lon);
        this.preRideTargetMarker = L.marker(target, { icon: this.stopIcon }).addTo(this.map);

        this.preRideLine = L.polyline(path).addTo(this.map);

        const b = this.preRideLine.getBounds();
        if (b.isValid()) this.map.fitBounds(b.pad(0.2));

        this.animateSelfAlongPreRide(path, 600);
      },
      error: (err: any) => {
        console.warn('Could not build route to pickup', err);
      },
    });
  }

  private animateSelfAlongPreRide(path: L.LatLng[], stepMs = 600) {
    if (!path.length) return;
    this.ensureSelfMarker();
    if (!this.selfMarker) return;

    const token = ++this.animToken;
    let i = 0;

    const tick = () => {
      if (token !== this.animToken) return;

      const p = path[i];
      if (!p) return;

      this.selfMarker!.setLatLng(p);
      this.selfPos = p;

      this.maybePushLocationToBackend(p);


      if (this.preRideLine) {
        const remaining = path.slice(i);
        this.preRideLine.setLatLngs(remaining);
      }

      i++;
      if (i < path.length) {
        this.animTimer = setTimeout(tick, stepMs);
      } else {
        this.animTimer = null;

        this.clearPreRideVisuals();

        (this.facade as any).clearDriveTo?.();
      }
    };

    tick();
  }

    private maybePushLocationToBackend(p: L.LatLng) {
      
    if (!this.authService.isLoggedIn()) return;

    const now = Date.now();
    if (now - this.lastLocationPushAt < this.locationPushEveryMs) return;
    this.lastLocationPushAt = now;

    const driverId = Number((this.authService as any).userId?.() ?? 0);
    if (!driverId) return;

    this.driverLocService.updateLocation(driverId, p.lat, p.lng).subscribe({
      error: () => {
      },
    });
  }


  // ===== LIVE RIDE =====

  private clearLiveRideVisuals() {
    if (this.liveRideLine) {
      this.map.removeLayer(this.liveRideLine);
      this.liveRideLine = null;
    }
    if (this.liveRideTargetMarker) {
      this.map.removeLayer(this.liveRideTargetMarker);
      this.liveRideTargetMarker = null;
    }
  }

  private startLiveRide(points: { lat: number; lon: number; label?: string }[]) {
    this.ensureSelfMarker();
    if (!this.selfMarker) return;

    this.stopSelfAnimation();
    this.clearPreRideVisuals();
    this.clearLiveRideVisuals();
    this.clearRoute();

    const pickup = L.latLng(points[0].lat, points[0].lon);
    this.selfMarker.setLatLng(pickup);
    this.selfPos = pickup;

    (this.routing as any).route(points).subscribe({
      next: (r: any) => {
        const geometry: GeoJSON.LineString | null = r?.geometry ?? r?.geojson ?? r;

        if (!geometry || geometry.type !== 'LineString') {
          console.warn('RoutingService returned unexpected geometry:', r);
          return;
        }

        const coords = (geometry.coordinates ?? []) as [number, number][];
        const path = coords.map(([x, y]) => L.latLng(y, x));

        if (path.length < 2) return;

        const dest = points[points.length - 1];
        this.liveRideTargetMarker = L.marker([dest.lat, dest.lon], { icon: this.endIcon }).addTo(this.map);

        this.liveRideLine = L.polyline(path).addTo(this.map);

        const b = this.liveRideLine.getBounds();
        if (b.isValid()) this.map.fitBounds(b.pad(0.2));

        this.animateSelfAlongLiveRide(path, 600);
      },
      error: (err: any) => {
        console.warn('route failed', err);
      },
    });
  }

private animateSelfAlongLiveRide(path: L.LatLng[], stepMs = 200) {
  if (!path.length) return;
  
  this.ensureSelfMarker(); 
  if (!this.selfMarker) return;

  const token = ++this.animToken;
  let i = 0;

  const tick = () => {
    if (token !== this.animToken) return;

    const p = path[i];
    if (!p) return;

    this.selfMarker!.setLatLng(p);
    this.selfPos = p;

    this.maybePushLocationToBackend(p);

    if (this.liveRideLine) {
      const remaining = path.slice(i);
      this.liveRideLine.setLatLngs(remaining);
    }

    i++;
    if (i < path.length) {
      this.animTimer = setTimeout(tick, stepMs);
    } else {
      this.animTimer = null;
      if (this.liveRideLine) {
        this.map.removeLayer(this.liveRideLine);
        this.liveRideLine = null;
      }
    }
  };

  tick();
}

private renderPassengerActiveRide(points: { lat: number; lon: number; label?: string }[]) {
  this.activePassengerMarkers.clearLayers();
  
  if (!this.map.hasLayer(this.activePassengerMarkers)) {
    this.activePassengerMarkers.addTo(this.map);
  }

  points.forEach((p, idx) => {
    const icon = idx === 0 ? this.startIcon : 
                 idx === points.length - 1 ? this.endIcon : 
                 this.stopIcon;

    L.marker([p.lat, p.lon], { icon }).addTo(this.activePassengerMarkers);
  });

  const bounds = L.latLngBounds(points.map(p => [p.lat, p.lon]));
  if (bounds.isValid()) this.map.fitBounds(bounds.pad(0.3));

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

  const role = this.authService.userRole();
  const myId = Number(this.authService.userId() ?? 0);

  let filteredLocations = locations.filter(l => {
    if (role === 'GUEST') return true;
    if (role === 'DRIVER') return l.driverId === myId;
    if (role === 'PASSENGER') {
      return this.targetDriverId ? l.driverId === this.targetDriverId : false;
    }
    return false;
  });

  const incomingIds = new Set(filteredLocations.map(l => l.driverId));

  for (const [id, marker] of this.driverMarkers.entries()) {
    if (!incomingIds.has(id)) {
      this.driversLayer.removeLayer(marker);
      this.driverMarkers.delete(id);
      if (id === myId) this.selfMarker = null;
    }
  }

  for (const loc of filteredLocations) {
    if (loc.driverId === myId && this.animTimer) continue; 

    let marker = this.driverMarkers.get(loc.driverId);
    const icon = loc.busy ? this.driverBusyIcon : this.driverFreeIcon;

    if (marker) {
      marker.setLatLng([loc.lat, loc.lng]);
      marker.setIcon(icon);
      
      if (loc.driverId === this.targetDriverId) {
        this.facade.setDriveTo(loc.driverId, loc.lat, loc.lng);
      }

      if (loc.driverId === myId && !this.animTimer) {
        this.selfPos = marker.getLatLng();
      }
    } else {
      marker = L.marker([loc.lat, loc.lng], { icon });
      this.driversLayer.addLayer(marker);
      this.driverMarkers.set(loc.driverId, marker);
      
      if (loc.driverId === myId) {
        this.selfMarker = marker;
      }
    }
  }
}
}
