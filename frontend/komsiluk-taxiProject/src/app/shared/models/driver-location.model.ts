export interface DriverLocation {
  driverId: number;
  lat: number;
  lng: number;
  updatedAt: string;
  busy: boolean;
  panic: boolean;
}

export interface DriverBasicDto {
  id: number;
  firstName: string;
  lastName: string;
}

