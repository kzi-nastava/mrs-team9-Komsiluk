import { AfterViewInit, Component, ElementRef, Input, inject, ViewChild, OnDestroy } from '@angular/core';
import * as L from 'leaflet';
import { forkJoin, of } from 'rxjs';
import { catchError, switchMap } from 'rxjs/operators';
import { GeocodingService } from '../../../../shared/components/map/services/geocoding.service';
import { GeoPoint, RoutingService } from '../../../../shared/components/map/services/routing.service';

@Component({
  selector: 'app-ride-details-map',
  standalone: true,
  templateUrl: './ride-details-map.component.html',
  styleUrls: ['./ride-details-map.component.css'],
})
export class RideDetailsMapComponent implements AfterViewInit, OnDestroy {
  @ViewChild('mapEl', { static: true }) mapEl!: ElementRef<HTMLDivElement>;
  @Input() locations: string[] = [];

  private map!: L.Map;
  private routeLayer: L.GeoJSON | null = null;
  
  private geoService = inject(GeocodingService);
  private routingService = inject(RoutingService);

  // 1. Definišemo tvoje ikonice iz assets foldera
  private baseIconOptions = {
    shadowUrl: 'assets/marker-shadow.png',
    iconAnchor: [12, 41] as L.PointExpression,
    popupAnchor: [1, -34] as L.PointExpression,
    shadowSize: [41, 41] as L.PointExpression
  };

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

  ngAfterViewInit(): void {
    this.initMap();
  }

  private initMap(): void {
    this.map = L.map(this.mapEl.nativeElement).setView([45.2671, 19.8335], 13);
    
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png').addTo(this.map);

    if (this.locations.length >= 2) {
      this.renderRealRoute();
    }
    
    setTimeout(() => this.map.invalidateSize(), 150);
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

        // 2. Logika za markere: prvi i poslednji su veliki, ostali (stanice) su mali
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

      // 3. Crtanje putanje sa bojom kao na glavnoj mapi (npr. #242424 ili plava)
      // Dodajemo "shadow" efekat liniji tako što iscrtamo deblju liniju ispod
      
      const shadowStyle = { color: '#000', weight: 8, opacity: 0.2 };
      const mainStyle = { color: '#242424', weight: 5, opacity: 0.9 }; // Promeni boju ovde ako treba

      L.geoJSON(routeResult.geometry as any, { style: shadowStyle }).addTo(this.map);

      this.routeLayer = L.geoJSON(routeResult.geometry as any, {
        style: mainStyle
      }).addTo(this.map);

      this.map.fitBounds(this.routeLayer.getBounds(), { padding: [40, 40] });
    });
  }

  ngOnDestroy(): void {
    this.map?.remove();
  }
}