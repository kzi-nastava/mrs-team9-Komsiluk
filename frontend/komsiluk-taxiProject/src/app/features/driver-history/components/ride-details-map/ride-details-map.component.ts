import { AfterViewInit, Component, ElementRef, Input, inject, ViewChild, OnDestroy, SimpleChanges, OnChanges } from '@angular/core';
import * as L from 'leaflet';
import { forkJoin, interval, of, Subscription } from 'rxjs';
import { catchError, switchMap } from 'rxjs/operators';
import { GeocodingService } from '../../../../shared/components/map/services/geocoding.service';
import { GeoPoint, RoutingService } from '../../../../shared/components/map/services/routing.service';
import { DriverLocationService } from '../../../../shared/components/map/services/driver-location.service';

@Component({
  selector: 'app-ride-details-map',
  standalone: true,
  templateUrl: './ride-details-map.component.html',
  styleUrls: ['./ride-details-map.component.css'],
})
export class RideDetailsMapComponent implements AfterViewInit, OnDestroy, OnChanges {
  @Input() driverId: number | null = null;
  @ViewChild('mapEl', { static: true }) mapEl!: ElementRef<HTMLDivElement>;
  @Input() locations: string[] = [];

  private map!: L.Map;
  private routeLayer: L.GeoJSON | null = null;
  
  private geoService = inject(GeocodingService);
  private routingService = inject(RoutingService);

  private driverService = inject(DriverLocationService);
  private driverMarker: L.Marker | null = null;
  private locationSub?: Subscription;

  private baseIconOptions = {
    shadowUrl: 'assets/marker-shadow.png',
    iconAnchor: [12, 41] as L.PointExpression,
    popupAnchor: [1, -34] as L.PointExpression,
    shadowSize: [41, 41] as L.PointExpression
  };

  
    private driverBusyIcon = L.divIcon({
      className: 'km-driver km-driver--busy',
      html: `<span class="material-symbols-outlined km-driver__icon">local_taxi</span>`,
      iconSize: [26, 26],
      iconAnchor: [13, 13],
    });

// ride-details-map.component.ts

private startLiveTracking() {
  // 1. Čistimo stari interval da ne dupliramo autiće
  if (this.locationSub) {
    this.locationSub.unsubscribe();
  }

  if (!this.driverId || !this.map) {
    console.error('LIVETRACKING ABORTED: Nedostaje ID ili MAPA', { id: this.driverId, map: !!this.map });
    return;
  }


  this.locationSub = interval(1000).pipe(
    switchMap(() => {
      console.log('Šaljem HTTP zahtev za lokaciju vozača:', this.driverId);
      return this.driverService.getOneDriverLocation(Number(this.driverId)).pipe(
        catchError(err => {
          console.error('HTTP Error u tracking-u:', err);
          return of(null);
        })
      );
    })
  ).subscribe({
    next: (loc) => {
      console.log('ODGOVOR SA BACKA:', loc);
      if (loc && loc.lat !== undefined && loc.lng !== undefined) {
        this.updateCarMarker(loc.lat, loc.lng, loc.busy);
      } else {
        console.warn('Backend vratio praznu lokaciju ili pogrešan format');
      }
    }
  });
}

private updateCarMarker(lat: number, lng: number, isBusy: boolean) {
  if (!this.map) return;
  
  // Ako marker postoji, samo ga pomeri
  if (this.driverMarker) {
    this.driverMarker.setLatLng([lat, lng]);
    console.log('Marker pomeren na:', lat, lng);
  } else {
    // Ako ne postoji, napravi ga
    this.driverMarker = L.marker([lat, lng], { 
      icon: this.driverBusyIcon,
      zIndexOffset: 2000 // Sigurno iznad rute i markera
    }).addTo(this.map);
    console.log('!!! PRVI PUT POSTAVLJEN AUTO NA MAPU !!!');
  }
}

  private largeIcon = L.icon({
    ...this.baseIconOptions,
    iconUrl: 'assets/marker-icon-2x.png',
    iconSize: [25, 41]
  });

  private smallIcon = L.icon({
    ...this.baseIconOptions,
    iconUrl: 'assets/marker-icon.png',
    iconSize: [20, 32],
    iconAnchor: [10, 32]
  });

 ngOnChanges(changes: SimpleChanges): void {
  // Proveravamo da li je driverId došao i da li je mapa spremna
  if (changes['driverId'] && this.driverId) {
    console.log('NG_ON_CHANGES: Driver ID detektovan:', this.driverId);
    if (this.map) {
      this.startLiveTracking();
    }
  }
}

  ngAfterViewInit(): void {
    this.initMap();
    // 3. Pokreni i ovde za svaki slučaj ako je ID već bio tu
    if (this.driverId) {
      this.startLiveTracking();
    }
  }
  private initMap(): void {
    if (this.map) return; // Spreči duplu inicijalizaciju

    this.map = L.map(this.mapEl.nativeElement).setView([45.2671, 19.8335], 13);
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png').addTo(this.map);

    if (this.locations && this.locations.length >= 2) {
      this.renderRealRoute();
    }
    
    // Forsiraj mapu da proveri veličinu kontejnera
    this.map.invalidateSize();
  }

  private renderRealRoute(): void {
    const geocodeObs = this.locations.map(loc => 
      this.geoService.lookupOne(loc).pipe(catchError(() => of(null)))
    );

    forkJoin(geocodeObs).pipe(
      switchMap(results => {
        const points: GeoPoint[] = results
          .filter(r => r !== null)
          .map(r => ({ lat: r!.lat, lon: r!.lon }));

        if (points.length < 2) return of(null);

        points.forEach((p, idx) => {
          const isStop = idx > 0 && idx < points.length - 1;
          const iconToUse = isStop ? this.smallIcon : this.largeIcon;

          L.marker([p.lat, p.lon], { icon: iconToUse })
            .addTo(this.map)
            .bindPopup(this.locations[idx]);
        });

        return this.routingService.route(points);
      })
    ).subscribe(routeResult => {
      if (!routeResult || !routeResult.geometry) return;

      if (this.routeLayer) this.map.removeLayer(this.routeLayer);

      
      const shadowStyle = { color: '#000', weight: 8, opacity: 0.2 };
      const mainStyle = { color: '#242424', weight: 5, opacity: 0.9 };

      L.geoJSON(routeResult.geometry as any, { style: shadowStyle }).addTo(this.map);

      this.routeLayer = L.geoJSON(routeResult.geometry as any, {
        style: mainStyle
      }).addTo(this.map);

      this.map.fitBounds(this.routeLayer.getBounds(), { padding: [40, 40] });
    });
  }

  ngOnDestroy(): void {
    this.locationSub?.unsubscribe();
    this.map?.remove();
  }
}