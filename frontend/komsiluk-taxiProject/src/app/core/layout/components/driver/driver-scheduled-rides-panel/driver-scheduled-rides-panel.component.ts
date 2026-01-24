import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../../auth/services/auth.service';
import { RideService } from '../../passenger/book_ride/services/ride.service'; 
import { RideResponseDTO } from '../../../../../shared/models/ride.models';

@Component({
  selector: 'app-driver-scheduled-rides-panel',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './driver-scheduled-rides-panel.component.html',
  styleUrl: '../driver-current-ride-panel/driver-current-ride-panel.component.css'
})
export class DriverScheduledRidesPanelComponent implements OnInit {
  private rideService = inject(RideService);
  private authService = inject(AuthService);
  
  scheduledRides = signal<RideResponseDTO[]>([]);
  loading = signal(true);

  constructor() {
    console.log('1. KOMPONENTA JE KREIRANA U MEMORIJI!');
  }

  ngOnInit() {
    console.log('2. ngOnInit je pokrenut!');
    this.loadScheduledData();
  }

  loadScheduledData() {
    const driverId = this.authService.userId();

    if (!driverId) {
      console.error("Driver ID not found in AuthService");
      this.loading.set(false);
      return;
    }

    this.loading.set(true);
    
    this.rideService.getScheduledRides(driverId).subscribe({
      next: (data) => {
        this.scheduledRides.set(data);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Error fetching scheduled rides:', err);
        this.loading.set(false);
      }
    });
  }

  asText(v: any): string {
    if (!v) return '';
    if (typeof v === 'string') return v;
    return v.address || v.name || '';
  }
}