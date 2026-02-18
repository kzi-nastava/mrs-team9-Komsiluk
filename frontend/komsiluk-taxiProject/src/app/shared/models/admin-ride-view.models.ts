import { VehicleType } from "./profile.models";

export interface AdminLiveRideCard {
  id: string;
  driverId: string; 
  driverName: string;
  date: string; 
  startTime: string;
  endTime: string; 
  pickup: string;
  destination: string;
  status: 'active' | 'pending' | 'in-progress' | 'finished' | 'cancelled';
  passengers: number;
  kilometers: number;
  durationText: string;
  price: number;
  mapImageUrl: string;
}

export interface AdminLiveRideDetailsVm {
  id: number;
  driverId: number | null;
  driverName: string;
  driverEmail: string;
  vehicleType: string;
  status: string;

  passengers: string[]; 

  pickupLocation: string; 
  destination: string;   
  stops: string[];      
  currentAddress?: string;

  kilometers: number;
  durationText: string;
  price: number;

  panicPressed: boolean;
  panicReason?: string;
  startTime: string; 
  endTime: string;  

}