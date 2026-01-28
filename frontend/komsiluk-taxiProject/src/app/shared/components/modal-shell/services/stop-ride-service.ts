// stop-ride-service.ts
import { inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { GeocodingService } from './../../map/services/geocoding.service';
import { RideProgressService, LabeledPoint } from './../../map/services/ride-progress-service';
import { firstValueFrom, forkJoin } from 'rxjs';
import { RideResponseDTO, StopRideRequestDTO } from '../../../models/ride.models';

@Injectable({ providedIn: 'root' })
export class StopRideService {
  private http = inject(HttpClient);
  private geo = inject(GeocodingService);
  private progress = inject(RideProgressService);

  open = signal(false);
  ride = signal<RideResponseDTO | null>(null);
  rideStoppedTrigger = signal<RideResponseDTO | null>(null);
  

  openModal(ride: RideResponseDTO) {
    this.ride.set(ride);
    this.open.set(true);
  }

  notifyRideStopped(ride: RideResponseDTO) {
  this.rideStoppedTrigger.set(ride);
}

  /**
   * Ključna metoda koja priprema podatke za backend
   */
  async prepareStopData(ride: RideResponseDTO): Promise<StopRideRequestDTO> {
    const driverId = ride.driverId;
    
    // 1. Dobavi trenutnu lokaciju vozača sa beka
    const loc = await firstValueFrom(this.http.get<any>(`http://localhost:8081/api/drivers/${driverId}/location`));
    const currentPoint = { lat: loc.lat, lon: loc.lng };

    // 2. Pretvori adrese vožnje (start, stops, end) u GeoPointse za kalkulaciju
    const addressStrings = [ride.startAddress, ...(ride.stops || []), ride.endAddress];
    const geoPointsObs = addressStrings.map(addr => this.geo.lookupOne(addr));
    const coords = await firstValueFrom(forkJoin(geoPointsObs));
    
    // Filtriramo null vrednosti i mapiramo u format koji ProgressService razume
    const routePoints = coords.filter(c => !!c).map(c => ({ lat: c!.lat, lon: c!.lon }));
    const stationPoints = coords.filter(c => !!c).map(c => ({ 
      lat: c!.lat, 
      lon: c!.lon, 
      label: this.trimLabel(c!.label) 
    } as LabeledPoint));


    // 3. Pozovi ProgressService da izračuna distancu i posećene stanice
    const result = await this.progress.stopRide(currentPoint, routePoints, stationPoints);

    // 4. Mapiraj u format za StopRideRequestDTO
  
    return {
      stopAddress: result.endPoint.label,
      visitedStops: result.visitedStops
        .map(s => s?.label)
        .filter(Boolean) as string[],
      distanceTravelledKm: result.distanceTravelledKm
  };
  }

  private trimLabel(fullLabel: string): string {
    if (!fullLabel) return '';
    const parts = fullLabel.split(',');
    // Uzimamo prva 3 dela adrese (npr. "Spens, Sutjeska 2, Novi Sad")
    // To eliminiše "Južnobatčki okrug, Vojvodina, 21000, Srbija"
    return parts.slice(0, 3).map(p => p.trim()).join(', ');
  }

  close() {
    this.open.set(false);
    this.ride.set(null);
  }
}