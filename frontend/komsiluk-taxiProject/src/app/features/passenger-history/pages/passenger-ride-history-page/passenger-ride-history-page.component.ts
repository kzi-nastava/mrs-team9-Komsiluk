
import { CommonModule } from '@angular/common';
import { Component, OnInit, signal, computed, effect, ChangeDetectorRef } from '@angular/core';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';

import { AuthService } from '../../../../core/auth/services/auth.service';
import { RideHistoryFilterPanelComponent } from '../../../../shared/components/ride-history-filter-panel/ride-history-filter-panel.component';
import { 
  PassengerRideHistoryApiService, 
  PassengerRideHistoryDTO, 
  PassengerRideDetailsDTO,
  PassengerRideSortBy 
} from '../../services/passenger-ride-history-api.service';
import { PassengerRideDetailsModalComponent } from '../../components/passenger-ride-details-modal/passenger-ride-details-modal.component';

type SortDirection = 'asc' | 'desc';

interface SortState {
  column: PassengerRideSortBy;
  direction: SortDirection;
}

@Component({
  selector: 'app-passenger-ride-history-page',
  standalone: true,
  imports: [
    CommonModule, 
    RideHistoryFilterPanelComponent,
    PassengerRideDetailsModalComponent
  ],
  templateUrl: './passenger-ride-history-page.component.html',
  styleUrls: ['./passenger-ride-history-page.component.css'],
})
export class PassengerRideHistoryPageComponent implements OnInit {
  // State
  rides = signal<PassengerRideHistoryDTO[]>([]);
  loading = signal(false);
  error = signal<string | null>(null);
  
  // Filter state
  filterFrom = signal('');
  filterTo = signal('');
  showFilterPanel = signal(false);
  
  // Sort state
  sortState = signal<SortState>({ column: 'DATE', direction: 'desc' });
  
  // Details modal
  detailsOpen = signal(false);
  selectedRideDetails = signal<PassengerRideDetailsDTO | null>(null);
  detailsLoading = signal(false);

  private userId: number | null = null;

  constructor(
    private apiService: PassengerRideHistoryApiService,
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.bootstrapUserId();
  }

    /**
   * Returns a formatted route string: Pickup: [start] → [station1] → ... → Destination: [end]
   * Only street names are shown (first part before comma). Stations omitted if none.
   */
  formatRoute(ride: PassengerRideHistoryDTO): string {
    // Capitalize for display
    const cap = (s: string) => s.charAt(0).toUpperCase() + s.slice(1);
    const start = cap(ride.startAddress.trim());
    const end = cap(ride.endAddress.trim());
    const stations = ride.route
      ? ride.route.split(',').map(s => cap(s.trim())).filter(s => s.length > 0)
      : [];
    let result = `${start}`;
    if (stations.length > 0) {
      result += ' → ' + stations.join(' → ');
    }
    result += ` → ${end}`;
    return result;
  }

  private bootstrapUserId(): void {
    const trySet = () => {
      const raw = this.authService.userId();
      const id = raw ? Number(raw) : NaN;

      if (!Number.isFinite(id) || id <= 0) {
        setTimeout(trySet, 50);
        return;
      }

      this.userId = id;
      this.loadRides();
    };

    trySet();
  }

  private loadRides(): void {
    if (!this.userId) return;

    this.loading.set(true);
    this.error.set(null);

    const sort = this.sortState();
    const sortBy = sort.column;

    this.apiService.getRides(
      this.userId,
      this.filterFrom() || undefined,
      this.filterTo() || undefined,
      sortBy
    ).subscribe({
      next: (data) => {
        // Apply frontend sorting for direction (backend might not support direction)
        const sorted = this.applySortDirection(data, sort);
        this.rides.set(sorted);
        this.loading.set(false);
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error loading rides:', err);
        this.error.set('Greška pri učitavanju istorije vožnji.');
        this.loading.set(false);
        this.cdr.detectChanges();
      }
    });
  }

  private applySortDirection(data: PassengerRideHistoryDTO[], sort: SortState): PassengerRideHistoryDTO[] {
    const sorted = [...data];
    const dir = sort.direction === 'asc' ? 1 : -1;

    sorted.sort((a, b) => {
      switch (sort.column) {
        case 'DATE':
        case 'START_TIME':
          return dir * (new Date(a.startTime).getTime() - new Date(b.startTime).getTime());
        case 'END_TIME':
          return dir * (new Date(a.endTime).getTime() - new Date(b.endTime).getTime());
        case 'START_ADDRESS':
        case 'ROUTE':
          // Sort by processed route string
          return dir * this.formatRoute(a).localeCompare(this.formatRoute(b));
        case 'END_ADDRESS':
          return dir * a.endAddress.localeCompare(b.endAddress);
        default:
          return 0;
      }
    });

    return sorted;
  }

  // === UI Actions ===

  toggleFilterPanel(): void {
    this.showFilterPanel.update(v => !v);
  }

  onFilterApplied(filter: { from: string; to: string }): void {
    this.filterFrom.set(filter.from);
    this.filterTo.set(filter.to);
    this.showFilterPanel.set(false);
    this.loadRides();
  }

  onHeaderClick(column: PassengerRideSortBy): void {
    const current = this.sortState();
    
    if (current.column === column) {
      // Toggle direction
      this.sortState.set({
        column,
        direction: current.direction === 'asc' ? 'desc' : 'asc'
      });
    } else {
      // New column, default to desc
      this.sortState.set({ column, direction: 'desc' });
    }
    
    this.loadRides();
  }

  getSortIcon(column: PassengerRideSortBy): string {
    const current = this.sortState();
    if (current.column !== column) return '↕';
    return current.direction === 'asc' ? '↑' : '↓';
  }

  onRowClick(ride: PassengerRideHistoryDTO): void {
    this.detailsLoading.set(true);
    this.detailsOpen.set(true);

    this.apiService.getRideDetails(ride.rideId).subscribe({
      next: (details) => {
        this.selectedRideDetails.set(details);
        this.detailsLoading.set(false);
      },
      error: (err) => {
        console.error('Error loading ride details:', err);
        this.detailsLoading.set(false);
        this.detailsOpen.set(false);
      }
    });
  }

  closeDetails(): void {
    this.detailsOpen.set(false);
    this.selectedRideDetails.set(null);
  }

  // === Formatting helpers ===

  formatDate(isoString: string): string {
    if (!isoString) return '—';
    const d = new Date(isoString);
    if (isNaN(d.getTime())) return '—';
    
    const dd = String(d.getDate()).padStart(2, '0');
    const mm = String(d.getMonth() + 1).padStart(2, '0');
    const yyyy = d.getFullYear();
    const hh = String(d.getHours()).padStart(2, '0');
    const min = String(d.getMinutes()).padStart(2, '0');
    
    return `${dd}.${mm}.${yyyy} ${hh}:${min}`;
  }
}
