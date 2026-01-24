export type VehicleType = 'STANDARD' | 'LUXURY' | 'VAN' | string;
export type DriverStatus = 'ACTIVE' | 'INACTIVE' | 'IN_RIDE';

export interface VehicleResponseDTO {
  id: number;
  model: string;
  type: VehicleType;
  licencePlate: string;
  seatCount: number;
  babyFriendly: boolean;
  petFriendly: boolean;
}

export interface VehicleCreateDTO {
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
    driverStatus?: DriverStatus;
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

export type DriverEditRequestResponseDTO = {
  id: number;
  requestedAt: string;
  status: 'PENDING' | 'APPROVED' | 'REJECTED';

  newName?: string | null;
  newSurname?: string | null;
  newAddress?: string | null;
  newCity?: string | null;
  newPhoneNumber?: string | null;

  newModel?: string | null;
  newType?: VehicleType | null;
  newLicencePlate?: string | null;
  newSeatCount?: number | null;
  newBabyFriendly?: boolean | null;
  newPetFriendly?: boolean | null;

  driverId: number;
};