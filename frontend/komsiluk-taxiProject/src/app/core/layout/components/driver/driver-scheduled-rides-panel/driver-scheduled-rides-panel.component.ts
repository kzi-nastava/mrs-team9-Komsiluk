import { Component, OnInit, signal, inject,EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../../auth/services/auth.service';
import { RideService } from '../../passenger/book_ride/services/ride.service'; 
import { RideResponseDTO } from '../../../../../shared/models/ride.models';
import { CancelRideDialogComponent } from '../../passenger/cancel_ride/cancel-ride-dialog.component';
import { CancelRideModalService } from '../../../../../shared/components/modal-shell/services/confirm-ride-modal-service';
import { ScheduledRidesService } from '../../../../../shared/components/modal-shell/services/scheduled-rides-service';

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
  private cancelModalSvc = inject(CancelRideModalService);

  private schedRidesService = inject(ScheduledRidesService);
  
  scheduledRides = signal<RideResponseDTO[]>([]);
  loading = signal(true);

  isCancelDialogOpen = false;
  selectedRideId?: number;

  openCancelDialog(rideId: number) {
    this.selectedRideId = rideId;
    this.isCancelDialogOpen = true;
  }

  closeCancelDialog() {
    this.isCancelDialogOpen = false;
    this.selectedRideId = undefined;
  }

  handleCancelConfirm(event: { rideId: number; reason: string }) {
    this.rideService.cancelRideDriver(event.rideId, event.reason);
    this.closeCancelDialog();
    
    // Opciono: Osveži listu odmah ili sačekaj reakciju servisa
    setTimeout(() => this.loadScheduledData(), 500); 
  }

  constructor() {
  }

  ngOnInit() {
    this.loadScheduledData();

    this.schedRidesService.refresh$.subscribe(() => {
      this.loadScheduledData();
    });
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

  onCancelRide(ride: RideResponseDTO) {
    this.cancelModalSvc.open(ride, 'driver');
  }
}