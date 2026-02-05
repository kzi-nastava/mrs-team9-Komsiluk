import { Component, OnInit, inject, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { interval, Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { AdminLiveRideCard, AdminLiveRideDetailsVm } from '../../../shared/models/admin-ride-view.models';
import { AdminLiveRideCardComponent } from '../admin-live-ride-card/admin-live-ride-card.component';
import { AdminLiveRideDetailsModalComponent } from '../admin-live-rides-details-modal/admin-live-ride-details-modal.component';
import { DriverLocationService } from '../../../shared/components/map/services/driver-location.service';

@Component({
  selector: 'app-admin-live-rides-page',
  standalone: true,
  imports: [CommonModule, FormsModule, AdminLiveRideCardComponent, AdminLiveRideDetailsModalComponent],
  templateUrl: './admin-live-rides-page.component.html',
  styleUrl: './admin-live-rides-page.component.css' 
})
export class AdminLiveRidesPageComponent implements OnInit, OnDestroy {
  private http = inject(HttpClient);
  private refreshSubscription: Subscription | undefined;
  private cdr = inject(ChangeDetectorRef); 
  private driverService = inject(DriverLocationService);

  allActiveRides: AdminLiveRideCard[] = [];
  filteredRides: AdminLiveRideCard[] = [];
  searchTerm: string = '';

  detailsOpen = false;
  detailsVm: AdminLiveRideDetailsVm | null = null;
  selectedRideId: string | null = null;
    driversBasic: any;

  ngOnInit() {
    this.loadDriversAndStartPolling();
  }

  loadDriversAndStartPolling() {
    this.driverService.getDriversBasic().subscribe({
      next: (drivers) => {
        this.driversBasic = drivers;
        this.startAutoRefresh();
      },
      error: (err) => {
        console.error('Greška pri učitavanju vozača:', err);
        this.startAutoRefresh(); 
      }
    });
  }

  ngOnDestroy() {
    this.refreshSubscription?.unsubscribe();
  }

  startAutoRefresh() {
    this.fetchActiveRides();
    this.refreshSubscription = interval(10000) // Osvežava svakih 10 sekundi
      .pipe(switchMap(() => this.http.get<any[]>('http://localhost:8081/api/rides/active/all')))
      .subscribe({
        next: (data) => {
          this.allActiveRides = data.map(dto => this.mapToCard(dto));
          this.onSearch();
        },
        error: (err) => console.error('Error fetching active rides:', err)
      });
  }

fetchActiveRides() {
  this.http.get<any[]>('http://localhost:8081/api/rides/active/all')
    .subscribe({
      next: (data) => {
        this.allActiveRides = data.map(dto => this.mapToCard(dto));
        this.onSearch(); 
        this.cdr.detectChanges();
      },
      error: (err) => console.error(err)
    });
}


  onSearch() {
  const term = this.searchTerm.toLowerCase().trim();
  
  if (!term) {
    this.filteredRides = [...this.allActiveRides];
  } else {
    this.filteredRides = this.allActiveRides.filter(r => 
      r.driverName.toLowerCase().includes(term)
    );
  }
}

onDetails(rideId: string | number) {
  this.selectedRideId = rideId.toString();
  const card = this.allActiveRides.find(r => r.id === rideId.toString());

  this.http.get<any>(`http://localhost:8081/api/rides/${Number(rideId)}`).subscribe({
    next: (dto) => {
      const emails: string[] = [];
      if (dto.creatorEmail) emails.push(dto.creatorEmail);
      if (dto.passengerEmails) emails.push(...dto.passengerEmails);

      // REŠENJE ZA STANICE: Splitujemo string po karakteru '|'
      let stopsArray: string[] = [];
      if (dto.route?.stops && typeof dto.route.stops === 'string') {
        stopsArray = dto.route.stops.split('|').map((s: string) => s.trim()).filter((s: string) => s.length > 0);
      } else if (Array.isArray(dto.route?.stops)) {
        stopsArray = dto.route.stops;
      }

      
      let estimatedEndTime = 'Pending';
    if (dto.startTime && dto.estimatedDurationMin) {
    const start = new Date(dto.startTime);
    // Dodajemo minute na startno vreme
    const end = new Date(start.getTime() + dto.estimatedDurationMin * 60000);
    estimatedEndTime = end.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    }

      this.detailsVm = {
        id: dto.id,
        driverId: dto.driverId || (card ? Number(card.driverId) : null),
        driverName: card?.driverName || 'N/A',
        driverEmail: dto.driverEmail || '',
        vehicleType: dto.vehicleType || 'STANDARD',
        status: dto.status,
        passengers: emails,
        
        pickupLocation: dto.route?.startAddress || 'N/A', 
        destination: dto.route?.endAddress || 'N/A',
        stops: stopsArray, // Sada je ovo sigurno niz celih adresa
        
        kilometers: dto.distanceKm,
        durationText: `${dto.estimatedDurationMin || 0} min`,
        price: dto.price,
        
        panicPressed: dto.panicTriggered || false,
        startTime: dto.startTime ? new Date(dto.startTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) : 'N/A',
        endTime: estimatedEndTime
    };
      
      this.detailsOpen = true;
      this.cdr.detectChanges();
    }
  });
}
 private mapToCard(dto: any): AdminLiveRideCard {
  const driverInfo = this.driversBasic.find((d: { id: any; }) => d.id === dto.driverId);
  
  const displayName = driverInfo 
    ? `${driverInfo.firstName} ${driverInfo.lastName}` 
    : (dto.driverEmail || `Driver #${dto.driverId}`);

  return {
    id: dto.id.toString(),
    driverId: dto.driverId?.toString() || '',
    driverName: displayName, 
    date: 'LIVE',
    startTime: dto.startTime ? new Date(dto.startTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) : '--:--',
    endTime: 'Pending',
    pickup: dto.startAddress || 'N/A',
    destination: dto.endAddress || 'N/A',
    status: 'active',
    passengers: dto.passengerEmails ? dto.passengerEmails.length : 0,
    kilometers: dto.distanceKm || 0,
    durationText: `${dto.estimatedDurationMin || 0} min`,
    price: dto.price || 0,
    mapImageUrl: 'assets/taxi.png'
  };
}

}