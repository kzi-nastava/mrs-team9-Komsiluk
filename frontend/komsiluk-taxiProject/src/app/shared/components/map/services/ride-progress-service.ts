import { Injectable } from '@angular/core';
import { GeoPoint } from './map-facade.service';
import { GeocodingService } from './geocoding.service';
import { filter, lastValueFrom, timeout } from 'rxjs';

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


  findClosestRouteIndex(route: GeoPoint[], current: GeoPoint, lastKnownIndex: number = 0): number {
  let minDist = Infinity;
  let closestIndex = lastKnownIndex;

  const searchStart = Math.max(0, lastKnownIndex - 10);
  
  for (let i = searchStart; i < route.length; i++) {
    const d = this.haversineDistance(route[i], current);
    if (d < minDist) {
      minDist = d;
      closestIndex = i;
    }
    else if (d > minDist + 100 && i > lastKnownIndex + 5) {
      break; 
    }
  }

  return closestIndex;
}

  calculateTravelledDistanceKm(route: GeoPoint[], endIndex: number): number {
    let meters = 0;

    for (let i = 1; i <= endIndex; i++) {
      meters += this.haversineDistance(route[i - 1], route[i]);
    }

    return +(meters / 1000).toFixed(2);
  }


  filterVisitedStops(
  route: GeoPoint[],
  endIndex: number,
  stops: Array<LabeledPoint | null>
): Array<LabeledPoint | null> {
  if (endIndex === 0) {
    return [stops[0]].filter(stop => stop !== null);
  } else if (endIndex === route.length - 1) {
    return stops.slice(
stops.length - 1
    ).filter(stop => stop !== null);
  }

  const visitedRoute = route.slice(0, endIndex + 1);
  const TOLERANCE_METERS = 70;

  return stops.filter((stop) => {
    if (!stop) return false;

    for (const point of visitedRoute) {
      if (this.haversineDistance(point, stop) < TOLERANCE_METERS) {
        return true;
      }
    }
    return false;
  });
}


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


  async stopRide(
    currentPoint: GeoPoint,
    route: GeoPoint[],
    stationPoints: LabeledPoint[]
  ): Promise<RouteProgressResult> {
    const endIndex = this.findClosestRouteIndex(route, currentPoint);


    const distanceTravelledKm = this.calculateTravelledDistanceKm(route, endIndex);

    const visitedStops = this.filterVisitedStops(route, endIndex, stationPoints);

    let finalLabel = '';
    const nearbyStation = stationPoints.find(s => this.haversineDistance(s, currentPoint) < 40);

    if (nearbyStation) {
      finalLabel = nearbyStation.label;
    } else {
      try {
        finalLabel = await lastValueFrom(this.geocodingService.reverseGeocode(currentPoint.lat, currentPoint.lon).pipe(timeout(2000)));
      } catch {
        finalLabel = `Stop near ${stationPoints[endIndex].label || 'current location'}`;
      }
    }

    return {
      endPoint: { lat: currentPoint.lat, lon: currentPoint.lon, label: this.trimLabel(finalLabel) },
      distanceTravelledKm,
      visitedStops: visitedStops.slice(1)
    };
  }
   private trimLabel(fullLabel: string): string {
    if (!fullLabel) return '';
    const parts = fullLabel.split(',');
    return parts.slice(0, 3).map(p => p.trim()).join(', ');
  }

}


