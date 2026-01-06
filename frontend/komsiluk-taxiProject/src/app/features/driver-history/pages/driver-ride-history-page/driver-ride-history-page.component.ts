import { Component, effect } from '@angular/core';
import { CommonModule } from '@angular/common';

import {
  RideHistoryCard,
  RideHistoryCardComponent
} from '../../components/ride-history-card/ride-history-card.component';

import { RideHistoryFilterService } from '../../services/driver-history-filter.service';

import {
  RideHistoryDetailsModalComponent,
  RideHistoryDetailsVm,
  PassengerRating
} from '../../components/ride-history-details-modal/ride-history-details-modal.component';

import { AuthService, UserRole } from '../../../../core/auth/services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-driver-ride-history-page',
  standalone: true,
  imports: [CommonModule, RideHistoryCardComponent, RideHistoryDetailsModalComponent],
  templateUrl: './driver-ride-history-page.component.html',
  styleUrls: ['./driver-ride-history-page.component.css'],
})
export class DriverRideHistoryPageComponent {
  rides: RideHistoryCard[] = [
    { id: '1', date: '13.12.2025', startTime: '12:00', endTime: '14:14', pickup: 'Brace Ribnikara 45', destination: 'Ilariona Ruvarca 27', status: 'completed', passengers: 3, kilometers: 100, durationText: '2h 14min', price: 200, mapImageUrl: 'assets/taxi.png' },
    { id: '2', date: '14.12.2025', startTime: '13:00', endTime: '15:00', pickup: 'Bulevar Oslobođenja 120', destination: 'Dr. Ivana Ribara 55', status: 'in-progress', passengers: 2, kilometers: 45, durationText: '1h 15min', price: 120, mapImageUrl: 'assets/taxi.png' },
    { id: '3', date: '15.12.2025', startTime: '16:00', endTime: '17:30', pickup: 'Cara Dušana 33', destination: 'Kralja Petra 71', status: 'completed', passengers: 1, kilometers: 30, durationText: '1h 30min', price: 80, mapImageUrl: 'assets/taxi.png' },
    { id: '4', date: '16.12.2025', startTime: '08:00', endTime: '09:45', pickup: 'Kneza Miloša 25', destination: 'Bulevar Zorana Đinđića 44', status: 'completed', passengers: 4, kilometers: 50, durationText: '1h 45min', price: 150, mapImageUrl: 'assets/taxi.png' },
    { id: '5', date: '17.12.2025', startTime: '10:30', endTime: '11:40', pickup: 'Vojvode Stepe 65', destination: 'Mihaila Pupina 22', status: 'completed', passengers: 2, kilometers: 55, durationText: '1h 10min', price: 140, mapImageUrl: 'assets/taxi.png' },
    { id: '6', date: '18.12.2025', startTime: '13:00', endTime: '14:10', pickup: 'Stevana Sremca 12', destination: 'Pasterova 18', status: 'canceled', passengers: 1, kilometers: 25, durationText: '1h 10min', price: 60, mapImageUrl: 'assets/taxi.png' },
    { id: '7', date: '19.12.2025', startTime: '14:30', endTime: '16:00', pickup: 'Svetogorska 22', destination: 'Kopaonik 10', status: 'in-progress', passengers: 3, kilometers: 120, durationText: '1h 30min', price: 300, mapImageUrl: 'assets/taxi.png' },
    { id: '8', date: '20.12.2025', startTime: '07:30', endTime: '09:00', pickup: 'Bulevar kralja Aleksandra 70', destination: 'Visnjiceva 10', status: 'completed', passengers: 2, kilometers: 80, durationText: '1h 30min', price: 190, mapImageUrl: 'assets/taxi.png' },
  ];

  userRole: UserRole;

  filteredRides: RideHistoryCard[] = [...this.rides];

  // --- MODAL STATE ---
  detailsOpen = false;
  detailsVm: RideHistoryDetailsVm | null = null;

  constructor(private filterSvc: RideHistoryFilterService, private auth: AuthService, private router: Router) {
    effect(() => {
      const { from, to } = this.filterSvc.range();
      this.filteredRides = this.applyDateFilter(this.rides, from, to);
    });
    this.userRole = this.auth.userRole();
    if (this.userRole !== UserRole.DRIVER) { // ovo treba izbaciti kada se implementiraju i istorije za ostale korisnike
      this.router.navigate(['/']);
    }
  }

  closeDetails() {
    this.detailsOpen = false;
    this.detailsVm = null;
  }

  private buildPassengers(count: number): string[] {
    const c = Math.max(0, Math.floor(count || 0));
    return Array.from({ length: c }, (_, i) => `user${i + 1}@gmail.com`);
  }

  private buildRatings(passengers: string[]): PassengerRating[] {
    // demo podaci kao na slici; posle možeš vezati na backend
    return passengers.map((email, idx) => {
      if (idx === 0) return { email, driverRating: 5, vehicleRating: 5, comment: 'Very pleasant experience!' };
      if (idx === 1) return { email, driverRating: 4, vehicleRating: 5, comment: null };
      return { email, driverRating: null, vehicleRating: null, comment: null };
    });
  }

  onDetails(id: string) {
    const ride = this.rides.find(r => r.id === id);
    if (!ride) return;

    const passengers = this.buildPassengers(ride.passengers);

    this.detailsVm = {
      passengers,
      ratings: this.buildRatings(passengers),

      // ako imaš map sliku, ovde stavi npr. 'assets/mock/map.png'
      mapImageUrl: ride.mapImageUrl,

      pickupLocation: ride.pickup,
      station1: 'Medical Faculty',
      station2: 'Suboticka 12',
      destination: ride.destination,
      startTime: ride.startTime,
      endTime: ride.endTime,

      kilometers: ride.kilometers,
      durationText: ride.durationText,
      price: ride.price,

      panicPressed: false,
      inconsistencyReport: null,
    };

    this.detailsOpen = true;
  }

  private parseDmy(dmy: string): Date | null {
    const parts = dmy.split('.');
    if (parts.length < 3) return null;
    const day = Number(parts[0]);
    const month = Number(parts[1]);
    const year = Number(parts[2]);
    if (!day || !month || !year) return null;
    return new Date(year, month - 1, day);
  }

  private parseIso(iso: string): Date | null {
    if (!iso) return null;
    const [y, m, d] = iso.split('-').map(Number);
    if (!y || !m || !d) return null;
    return new Date(y, m - 1, d);
  }

  private applyDateFilter(rides: RideHistoryCard[], from: string, to: string) {
    const fromDate = this.parseIso(from);
    const toDate = this.parseIso(to);
    if (toDate) toDate.setHours(23, 59, 59, 999);

    if (!fromDate && !toDate) return [...rides];

    return rides.filter(r => {
      const rideDate = this.parseDmy(r.date);
      if (!rideDate) return false;

      const okFrom = fromDate ? rideDate >= fromDate : true;
      const okTo = toDate ? rideDate <= toDate : true;
      return okFrom && okTo;
    });
  }
}
