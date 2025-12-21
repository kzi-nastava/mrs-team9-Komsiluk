import { Component, AfterViewInit } from '@angular/core';
import * as L from 'leaflet';

@Component({
  selector: 'app-map',
  standalone: true,
  templateUrl: './map.html',
  styleUrls: ['./map.css'],
})
export class MapComponent implements AfterViewInit {
  private map!: L.Map;

  private initMap(): void {
    // Grube granice Novog Sada (možeš kasnije da ih upeglaš)
    const noviSadBounds = L.latLngBounds(
      [45.214, 19.764], // SW
      [45.309, 19.929]  // NE
    );

    this.map = L.map('map', {
      center: [45.2671, 19.8335], // Novi Sad centar
      zoom: 13,
      minZoom: 12,
      maxZoom: 18,

      // ključni deo:
      maxBounds: noviSadBounds,
      maxBoundsViscosity: 1.0, // 1.0 = “ne pušta” van granica
    });

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 18,
      attribution: '&copy; OpenStreetMap contributors',
    }).addTo(this.map);

    // da odmah “legne” u bounds (opciono, ali lepo)
    this.map.fitBounds(noviSadBounds);
  }

  ngAfterViewInit(): void {
    this.initMap();

    // ponekad treba kad je layout flex/resize:
    setTimeout(() => this.map.invalidateSize(), 0);
  }
}
