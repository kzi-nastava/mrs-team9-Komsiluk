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
