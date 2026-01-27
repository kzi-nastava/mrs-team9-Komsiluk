import { VehicleType } from "./profile.models";

export type RideStatus = 'REJECTED' | 'REQUESTED' | 'ASSIGNED' | 'SCHEDULED' | 'ACTIVE' | 'FINISHED' | 'CANCELED';

export interface RideCreateDTO {
  creatorId: number;
  startAddress: string;
  endAddress: string;
  stops: string[];
  distanceKm: number;
  estimatedDurationMin: number;
  vehicleType: VehicleType;
  babyFriendly: boolean;
  petFriendly: boolean;
  scheduledAt: string | null;
  passengerEmails: string[];
  startLat: number;
  startLng: number;
}

export interface RideResponseDTO {
  id: number;
  status: RideStatus;
  createdAt: string;
  scheduledAt: string | null;
  startTime: string | null;
  endTime: string | null;
  price: number;
  routeId: number;
  driverId: number | null;
  passengerIds: number[];
  startAddress: string;
  endAddress: string;
  stops: string[];
  panicTriggered: boolean;
  cancellationSource: string | null;
  cancellationReason: string | null;
  creatorId: number;
  distanceKm: number;
  estimatedDurationMin: number;
  vehicleType: VehicleType;
  babyFriendly: boolean;
  petFriendly: boolean;
}

export interface RidePassengerActiveDTO {
  driverId: number;
  rideId: number;
  driverFirstName: string;
  driverLastName: string;
  driverEmail: string;
  startAddress: string;
  endAddress: string;
  stops: string[];
  status: RideStatus;
}