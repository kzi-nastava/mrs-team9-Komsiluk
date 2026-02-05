import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RideService } from '../../../core/layout/components/passenger/book_ride/services/ride.service';
import { RideDetailsMapComponent } from '../../driver-history/components/ride-details-map/ride-details-map.component';

@Component({
  selector: 'app-admin-live-ride-details-modal',
  standalone: true,
  imports: [CommonModule,RideDetailsMapComponent],
  templateUrl: './admin-live-ride-details-modal.component.html',
  styleUrl: './admin-live-ride-details-modal.component.css'
})
export class AdminLiveRideDetailsModalComponent implements OnInit, OnChanges {
  @Input({ required: true }) data!: any;
  @Output() close = new EventEmitter<void>();

  private rideService = inject(RideService);
  inconsistencyReports = signal<any[]>([]);

 ngOnInit(): void {
    this.checkAndLoad();
  }

  // OVO JE KLJUČNO: Ako se data promeni, ponovo učitaj reporte
  ngOnChanges(changes: SimpleChanges): void {
    if (changes['data'] && this.data) {
      console.log('MODAL PRIMIO PODATKE:', this.data);
      console.log('VOZAČ ID ZA MAPU:', this.data.driverId);
      this.checkAndLoad();
    }
  }

  private checkAndLoad() {
    if (this.data && this.data.id) {
      console.log('Učitavam Inconsistencies za vožnju ID:', this.data.id);
      this.loadInconsistencyReports();
    }
  }

  loadInconsistencyReports() {
    this.rideService.getInconsistencyReports(this.data.id).subscribe({
      next: (reports) => {
        this.inconsistencyReports.set(reports);
      },
      error: (err) => console.error('Error loading reports:', err)
    });
  }

  get allLocations(): string[] {
    if (!this.data) return [];
    const validStops = (this.data.stops || []).filter((s: any) => !!s);
    return [this.data.pickupLocation, ...validStops, this.data.destination];
  }

  onClose() {
    this.close.emit();
  }

}