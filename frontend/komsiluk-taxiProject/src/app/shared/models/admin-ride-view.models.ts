import { VehicleType } from "./profile.models"; // Pretpostavka da je VehicleType ovde

export interface AdminLiveRideCard {
  id: string;
  driverId: string; // Dodali smo ID vozača za pretragu
  driverName: string;
  date: string; // npr. 'LIVE' ili 'Active since 12:30'
  startTime: string;
  endTime: string; // 'Pending' ili 'Expected XX:XX'
  pickup: string;
  destination: string;
  status: 'active' | 'pending' | 'in-progress' | 'finished' | 'cancelled'; // Pojednostavljeni statusi za prikaz
  passengers: number;
  kilometers: number;
  durationText: string;
  price: number;
  mapImageUrl: string;
}

export interface AdminLiveRideDetailsVm {
  // Osnovno - ID mora biti Long (number) kao na backu
  id: number;
  driverId: number | null;
  driverName: string;
  driverEmail: string;
  vehicleType: string;
  status: string;

  // PUTNICI - Promeni u string[] jer backend šalje emailove!
  passengers: string[]; 

  // RUTA - Koristi imena polja koja tvoj modal HTML već traži
  pickupLocation: string; // Umesto startAddress
  destination: string;    // Umesto endAddress
  stops: string[];        // Waypoints/Stops sa backenda
  currentAddress?: string;

  // STATISTIKA
  kilometers: number;
  durationText: string;
  price: number;

  // PANIC & TIMES
  panicPressed: boolean;
  panicReason?: string;
  startTime: string; // Umesto startedAt
  endTime: string;   // Umesto finishedAt

}