export interface RouteCreateDTO {
  startAddress: string;
  endAddress: string;
  stops: string;
  distanceKm: number;
  estimatedDurationMin: number;
}

export interface RouteResponseDTO {
  id: number;
  startAddress: string;
  endAddress: string;
  stops: string;
  distanceKm: number;
  estimatedDurationMin: number;
}
