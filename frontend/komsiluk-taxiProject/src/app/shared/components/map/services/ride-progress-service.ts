import { Injectable } from '@angular/core';
import { GeoPoint } from './map-facade.service';
import { GeocodingService } from './geocoding.service';

export interface LabeledPoint {
  lat: number;
  lon: number;
  label: string;
}

export interface RouteProgressResult {
  endPoint: LabeledPoint;
  distanceTravelledKm: number;
  visitedStops: Array<LabeledPoint | null>;
}

@Injectable({
  providedIn: 'root',
})
export class RideProgressService {
  private readonly EARTH_RADIUS_M = 6371000;

  constructor(private geocodingService: GeocodingService) {}

  /**
   * Pronalazi indeks tačke na ruti koja je najbliža trenutnoj poziciji vozila
   */
  findClosestRouteIndex(route: GeoPoint[], current: GeoPoint): number {
    let minDist = Infinity;
    let closestIndex = 0;

    route.forEach((p, i) => {
      const d = this.haversineDistance(p, current);
      if (d < minDist) {
        minDist = d;
        closestIndex = i;
      }
    });

    return closestIndex;
  }

  /**
   * Izračunava pređenu kilometražu do endIndex-a
   */
  calculateTravelledDistanceKm(route: GeoPoint[], endIndex: number): number {
    let meters = 0;

    for (let i = 1; i <= endIndex; i++) {
      meters += this.haversineDistance(route[i - 1], route[i]);
    }

    return +(meters / 1000).toFixed(2);
  }

  /**
   * Filtrira koje stanice su zaista posjećene do trenutne tačke
   */
  filterVisitedStops(
    route: GeoPoint[],
    endIndex: number,
    stops: Array<LabeledPoint | null>
  ): Array<LabeledPoint | null> {
    const visitedRoute = route.slice(0, endIndex + 1);

    return stops.filter((stop) => {
      if (!stop) return false;

      return visitedRoute.some((p) =>
        this.haversineDistance(p, stop) < 50 // tolerance 50m
      );
    });
  }

  /**
   * Haversine formula za rastojanje između dve geokoordinate
   */
  private haversineDistance(a: GeoPoint, b: GeoPoint): number {
    const dLat = this.toRad(b.lat - a.lat);
    const dLon = this.toRad(b.lon - a.lon);

    const lat1 = this.toRad(a.lat);
    const lat2 = this.toRad(b.lat);

    const h =
      Math.sin(dLat / 2) ** 2 +
      Math.cos(lat1) * Math.cos(lat2) *
      Math.sin(dLon / 2) ** 2;

    return 2 * this.EARTH_RADIUS_M * Math.asin(Math.sqrt(h));
  }

  private toRad(v: number): number {
    return (v * Math.PI) / 180;
  }

  /**
   * Glavna metoda koja vraća rezultat za zaustavljanje vožnje
   */
  async stopRide(
    currentPoint: GeoPoint,
    route: GeoPoint[],
    stationPoints: Array<LabeledPoint | null>
  ): Promise<RouteProgressResult> {
    const endIndex = this.findClosestRouteIndex(route, currentPoint);

    const distanceTravelledKm = this.calculateTravelledDistanceKm(route, endIndex);

    const visitedStops = this.filterVisitedStops(route, endIndex, stationPoints);

    const rawEndPoint = route[endIndex];

    let label = rawEndPoint.label;
    if (!label || label.trim() === '') {

      label = await this.geocodingService.reverseGeocode(rawEndPoint.lat, rawEndPoint.lon).toPromise();
    }

    const endPoint: LabeledPoint = {
      lat: rawEndPoint.lat,
      lon: rawEndPoint.lon,
      label: label ?? 'Unknown address',
    };

    return {
      endPoint,
      distanceTravelledKm,
      visitedStops,
    };
  }

}


// ovako treba da ga mapiram u json da bih ga poslao serveru
// {
// stopAddress: result.endPoint.label,
//   visitedStops: result.visitedStops
//     .map(s => s?.label)
//     .join(','),
//     distanceTravelledKm: result.distanceTravelledKm
// }

