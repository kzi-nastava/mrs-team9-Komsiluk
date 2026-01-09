export type DailyValueDTO = {
  date: string;
  value: number;
};

export type RideReportDTO = {
  ridesPerDay: DailyValueDTO[];
  totalRides: number;
  averageRidesPerDay: number;

  distancePerDay: DailyValueDTO[];
  totalDistanceKm: number;
  averageDistanceKmPerDay: number;

  moneyPerDay: DailyValueDTO[];
  totalMoney: number | string;
  averageMoneyPerDay: number | string;
};
