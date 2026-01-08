import { VehicleType } from './profile.models';

export interface FavoriteRouteCreateDTO {
  title: string;
  routeId: number;
  userId?: number;
  passengersEmails: string[];
  vehicleType: VehicleType;
  petFriendly: boolean;
  babyFriendly: boolean;
}

export interface FavoriteRouteResponseDTO {
  id: number;
  title: string;
  routeId: number;
  startAddress: string;
  endAddress: string;
  stops: string[];
  passengerIds: number[];
  vehicleType: VehicleType;
  petFriendly: boolean;
  babyFriendly: boolean;
  distanceKm: number;
  estimatedDurationMin: number;
}

export interface FavoriteRouteUpdateDTO {
  title: string;
}
