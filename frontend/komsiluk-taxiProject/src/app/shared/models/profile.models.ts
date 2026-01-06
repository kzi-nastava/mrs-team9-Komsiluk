export type VehicleType = 'STANDARD' | 'LUXURY' | 'VAN' | string;

export interface VehicleResponseDTO {
  id: number;
  model: string;
  type: VehicleType;
  licencePlate: string;
  seatCount: number;
  babyFriendly: boolean;
  petFriendly: boolean;
}

export interface UserProfileResponseDTO {
  email: string;
  firstName: string;
  lastName: string;
  address: string;
  city: string;
  phoneNumber: string;
  profileImageUrl: string;
  vehicle: VehicleResponseDTO | null;
  activeMinutesLast24h: number;
}

export interface UserProfileUpdateDTO {
  firstName: string;
  lastName: string;
  address: string;
  city: string;
  phoneNumber: string;
  profileImageUrl?: string | null;
}

export interface DriverEditRequestCreateDTO {
  newName: string;
  newSurname: string;
  newAddress: string;
  newCity: string;
  newPhoneNumber: string;
  newProfileImageUrl?: string | null;

  newModel: string;
  newType: VehicleType;
  newLicencePlate: string;
  newSeatCount: number;
  newBabyFriendly: boolean;
  newPetFriendly: boolean;
}

export interface DriverEditRequestResponseDTO {
  id: number;
  requestedAt: string;
  status: string;
  driverId: number;

  // no need for other fields right now
}