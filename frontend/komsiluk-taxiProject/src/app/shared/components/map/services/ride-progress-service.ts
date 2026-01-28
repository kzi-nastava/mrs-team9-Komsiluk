import { Injectable } from '@angular/core';
import { GeoPoint } from './map-facade.service';
import { GeocodingService } from './geocoding.service';
import { lastValueFrom, timeout } from 'rxjs';

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
  findClosestRouteIndex(route: GeoPoint[], current: GeoPoint, lastKnownIndex: number = 0): number {
  let minDist = Infinity;
  let closestIndex = lastKnownIndex;

  // Optimizacija: Ne pretražuj celu rutu od 1000 tačaka svaki put.
  // Pretražuj od poslednjeg poznatog indeksa + mali prozor unazad (za svaki slučaj)
  const searchStart = Math.max(0, lastKnownIndex - 10);
  
  for (let i = searchStart; i < route.length; i++) {
    const d = this.haversineDistance(route[i], current);
    if (d < minDist) {
      minDist = d;
      closestIndex = i;
    }
    // Ako distanca krene naglo da raste, znači da smo prošli najbližu tačku
    // i nema potrebe da proveravamo preostalih 500 tačaka rute (Early Exit)
    else if (d > minDist + 100 && i > lastKnownIndex + 5) {
      break; 
    }
  }

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
  const TOLERANCE_METERS = 70; // Povećano na 70m zbog preciznosti GPS-a u gradu

  return stops.filter((stop) => {
    if (!stop) return false;

    // Umesto "some", koristimo prostiji loop koji možemo prekinuti
    for (const point of visitedRoute) {
      if (this.haversineDistance(point, stop) < TOLERANCE_METERS) {
        return true; // Stanica je "zakačena" na ruti
      }
    }
    return false;
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
    stationPoints: LabeledPoint[]
  ): Promise<RouteProgressResult> {
    // 1. Pronađi dokle je vozač stigao na isplaniranoj ruti
    const endIndex = this.findClosestRouteIndex(route, currentPoint);


    // 2. Izračunaj stvarnu pređenu distancu po putanji rute
    const distanceTravelledKm = this.calculateTravelledDistanceKm(route, endIndex);

    // 3. Odredi koje su stanice posećene (unutar 50m od putanje)
    const visitedStops = this.filterVisitedStops(route, endIndex, stationPoints);

    // 4. Odredi labelu za STOP adresu (prioritet: stanica blizu -> reverse geocode -> fallback)
    let finalLabel = '';
    const nearbyStation = stationPoints.find(s => this.haversineDistance(s, currentPoint) < 40);

    if (nearbyStation) {
      finalLabel = nearbyStation.label;
    } else {
      try {
        // Koristimo lastValueFrom jer je Nominatim asinhron
        finalLabel = await lastValueFrom(this.geocodingService.reverseGeocode(currentPoint.lat, currentPoint.lon).pipe(timeout(2000)));
      } catch {
        finalLabel = `Stop near ${stationPoints[endIndex].label || 'current location'}`;
      }
    }

    return {
      endPoint: { lat: currentPoint.lat, lon: currentPoint.lon, label: this.trimLabel(finalLabel) },
      distanceTravelledKm,
      visitedStops
    };
  }
   private trimLabel(fullLabel: string): string {
    if (!fullLabel) return '';
    const parts = fullLabel.split(',');
    // Uzimamo prva 3 dela adrese (npr. "Spens, Sutjeska 2, Novi Sad")
    // To eliminiše "Južnobatčki okrug, Vojvodina, 21000, Srbija"
    return parts.slice(0, 3).map(p => p.trim()).join(', ');
  }

}


