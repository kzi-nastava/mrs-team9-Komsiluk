import { Injectable, computed, signal } from '@angular/core';
import { RoutingService, GeoPoint, RouteResult } from './routing.service';
import { MapFacadeService } from './map-facade.service';
import { VehicleType } from '../../../models/profile.models';

@Injectable({ providedIn: 'root' })
export class RidePlannerService {


  private pickupTextSig = signal('');
  private destinationTextSig = signal('');

  pickupText = computed(() => this.pickupTextSig());
  destinationText = computed(() => this.destinationTextSig());

  setPickupText(v: string) {
    this.pickupTextSig.set(v);
  }

  setDestinationText(v: string) {
    this.destinationTextSig.set(v);
  }

  private pickupSig = signal<GeoPoint | null>(null);
  private destSig = signal<GeoPoint | null>(null);
  private stopsSig = signal<GeoPoint[]>([]);
  private vehicleTypeSig = signal<VehicleType>('STANDARD');

  private routeSig = signal<RouteResult | null>(null);

  pickup = computed(() => this.pickupSig());
  destination = computed(() => this.destSig());
  stops = computed(() => this.stopsSig());
  vehicleType = computed(() => this.vehicleTypeSig());

  route = computed(() => this.routeSig());

  km = computed(() => this.routeSig() ? this.routeSig()!.distanceMeters / 1000 : 0);
  minutes = computed(() => this.routeSig() ? Math.round(this.routeSig()!.durationSeconds / 60) : 0);
  price = computed(() => {
    const km = this.km();
    const t = this.vehicleTypeSig();

    // here should be backend call to get real price, but for demo purposes we use simple formula
    const base = t === 'STANDARD' ? 150 : t === 'LUXURY' ? 250 : 300;
    const perKm = t === 'STANDARD' ? 80 : t === 'LUXURY' ? 120 : 140;

    return Math.round(base + km * perKm);
  });

  constructor(
    private routing: RoutingService,
    private mapFacade: MapFacadeService
  ) { }

  setPickup(p: GeoPoint | null) {
    this.pickupSig.set(p);
    this.recalculate();
  }
  setDestination(p: GeoPoint | null) {
    this.destSig.set(p);
    this.recalculate();
  }
  setStops(stops: GeoPoint[]) {
    this.stopsSig.set(stops);
    this.recalculate();
  }
  setVehicleType(t: VehicleType) {
    this.vehicleTypeSig.set(t);
  }

  private recalculate() {
    const pickup = this.pickupSig();
    const dest = this.destSig();
    if (!pickup || !dest) {
      this.routeSig.set(null);
      this.mapFacade.setState(null);
      return;
    }

    const points = [pickup, ...this.stopsSig(), dest];

    this.routing.route(points).subscribe({
      next: (r) => {
        queueMicrotask(() => {
          this.routeSig.set(r);
          this.mapFacade.setState({ points, geometry: r.geometry });
        });
      },
      error: () => {
        queueMicrotask(() => {
          this.routeSig.set(null);
          this.mapFacade.setState(null);
        });
      }
    });
  }

  reset(): void {
    this.pickupTextSig.set('');
    this.destinationTextSig.set('');

    this.pickupSig.set(null);
    this.destSig.set(null);
    this.stopsSig.set([]);
    this.routeSig.set(null);

    this.mapFacade.setState(null);
  }

}
