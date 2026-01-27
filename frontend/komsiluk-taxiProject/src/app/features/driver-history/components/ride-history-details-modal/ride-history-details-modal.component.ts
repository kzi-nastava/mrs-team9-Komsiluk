import { CommonModule } from '@angular/common';
import { Component, EventEmitter, HostListener, inject, Input, OnInit, Output, signal } from '@angular/core';
import { RideDetailsMapComponent } from '../ride-details-map/ride-details-map.component';
import { RideService } from '../../../../core/layout/components/passenger/book_ride/services/ride.service';
import { RatingResponseDTO, RatingService } from '../../services/rating.service';

export interface PassengerRating {
  email: string; // Backend trenutno ne vraca email, mapiracemo ID
  driverRating?: number | null;
  vehicleRating?: number | null;
  comment?: string | null;
}

export interface RideHistoryDetailsVm {
  // Left
  id: number;
  passengers: string[];
  // ratings: PassengerRating[]; // Ovo vise ne moramo uzimati iz inputa ako fetchujemo, ali neka stoji za svaki slucaj

  // Center
  mapImageUrl: string;

  // Right
  pickupLocation: string;
  stops: string[];
  destination: string;
  startTime: string;
  endTime: string;

  // Bottom right stats
  kilometers: number;
  durationText: string;
  price: number;

  // Bottom center
  panicPressed: boolean;
  inconsistencyReport?: string | null;

  statusText?: string | null;             
  cancellationSource?: string | null;    
  cancellationReason?: string | null;
}

@Component({
  selector: 'app-ride-history-details-modal',
  standalone: true,
  imports: [CommonModule, RideDetailsMapComponent],
  templateUrl: './ride-history-details-modal.component.html',
  styleUrls: ['./ride-history-details-modal.component.css'],
})
export class RideHistoryDetailsModalComponent implements OnInit {
  @Input({ required: true }) data!: RideHistoryDetailsVm;
  @Output() close = new EventEmitter<void>();

  private rideService = inject(RideService);
  private ratingService = inject(RatingService); // Injektujemo novi servis

  inconsistencyReports = signal<any[]>([]);
  ratings = signal<PassengerRating[]>([]);
  ratingsLoading = signal(false);

  onBackdropClick() {
    this.close.emit();
  }

  onDialogClick(e: MouseEvent) {
    e.stopPropagation();
  }

  @HostListener('document:keydown.escape')
  onEsc() {
    this.close.emit();
  }

  fmtRating(v?: number | null) {
    return v == null ? 'N/A' : `${v} stars`;
  }

  fmtText(v?: string | null) {
    return v && v.trim().length ? v : 'N/A';
  }

  isCanceled(): boolean {
    return (this.data.statusText ?? '').toUpperCase().includes('CANCEL');
  }

  get allLocations(): string[] {
    if (!this.data) return [];
    
    const validStops = (this.data.stops || []).filter(s => !!s);
    
    return [
      this.data.pickupLocation,
      ...validStops,
      this.data.destination
    ];
  }

  ngOnInit(): void {
    if (this.data && this.data.id) {
      this.loadInconsistencyReports();
      this.loadRatings();
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

  loadRatings() {
  if (!this.data?.id) return;

  this.ratingsLoading.set(true);
  this.ratingService.getRatingsForRide(this.data.id).subscribe({
    next: (dtos: RatingResponseDTO[]) => {
      const mapped: PassengerRating[] = (dtos ?? []).map(dto => ({
        // dok backend ne šalje email, prikaži ID
        email: dto.raterMail || `Rater ID: ${dto.raterId}`,
        driverRating: dto.driverGrade,
        vehicleRating: dto.vehicleGrade,
        comment: dto.comment,
      }));

      this.ratings.set(mapped);
      this.ratingsLoading.set(false);
    },
    error: (err: any) => {
      console.error('Error loading ratings:', err);
      this.ratingsLoading.set(false);
    }
  });
  }
}