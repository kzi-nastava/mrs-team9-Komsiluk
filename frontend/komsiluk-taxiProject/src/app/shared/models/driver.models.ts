import { VehicleCreateDTO, VehicleType } from "./profile.models";

export type DriverCreateDTO = {
  firstName: string;
  lastName: string;
  address: string;
  city: string;
  phoneNumber: string;
  email: string;
  profileImageUrl?: string | null;
  vehicle: VehicleCreateDTO;
};

export type DriverResponseDTO = {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  address: string;
  city: string;
  phoneNumber: string;
  profileImageUrl: string;
  active: boolean;
  blocked: boolean;
  createdAt: string;
  role: string;
  driverStatus: string;
  vehicle: any;
};
