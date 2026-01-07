import { Component, AfterViewInit, effect, inject, Injector, runInInjectionContext } from '@angular/core';
import * as L from 'leaflet';
import { MapFacadeService } from './services/map-facade.service';

@Component({
  selector: 'app-map',
  standalone: true,
  templateUrl: './map.html',
  styleUrls: ['./map.css'],
})
export class MapComponent implements AfterViewInit {
  private map!: L.Map;
  private injector = inject(Injector);

  private facade = inject(MapFacadeService);
  private markersLayer = L.layerGroup();
  private routeLayer: L.GeoJSON | null = null;

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
}
