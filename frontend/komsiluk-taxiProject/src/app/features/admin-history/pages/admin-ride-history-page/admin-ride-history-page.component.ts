import { Component, inject, signal, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { RideHistoryFilterPanelComponent } from '../../../../shared/components/ride-history-filter-panel/ride-history-filter-panel.component';
import { AdminRideDetailsModalComponent } from '../../components/admin-ride-details-modal/admin-ride-details-modal.component';
import {
  AdminRideHistoryApiService,
  AdminRideHistoryDTO,
  AdminRideDetailsDTO,
  AdminRideSortBy
} from '../../services/admin-ride-history-api.service';

type SortDirection = 'asc' | 'desc';

interface SortState {
  column: AdminRideSortBy | null;
  direction: SortDirection;
}

@Component({
  selector: 'app-admin-ride-history-page',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RideHistoryFilterPanelComponent,
    AdminRideDetailsModalComponent
  ],
  templateUrl: './admin-ride-history-page.component.html',
  styleUrls: ['./admin-ride-history-page.component.css']
})
export class AdminRideHistoryPageComponent {
  private apiService = inject(AdminRideHistoryApiService);
  private cdr = inject(ChangeDetectorRef);

  // Search
  searchEmail = '';
  currentEmail = signal<string | null>(null);

  // Rides data
  rides = signal<AdminRideHistoryDTO[]>([]);
  loading = signal(false);
  error = signal<string | null>(null);
  searched = signal(false);

  // Filter
  filterOpen = signal(false);
  filterFrom = signal('');
  filterTo = signal('');

  // Sorting
  sortState = signal<SortState>({ column: 'DATE', direction: 'desc' });

  // Details modal
  detailsOpen = signal(false);
  detailsLoading = signal(false);
  selectedRideDetails = signal<AdminRideDetailsDTO | null>(null);

  onSearch(): void {
    const email = this.searchEmail.trim();
    if (!email) {
      this.error.set('Please enter an email address.');
      return;
    }

    this.currentEmail.set(email);
    this.searched.set(true);
    this.loadRides();
  }

  private loadRides(): void {
    const email = this.currentEmail();
    if (!email) return;

    this.loading.set(true);
    this.error.set(null);

    const sort = this.sortState();
    const sortBy = sort.column ?? 'DATE';

    this.apiService.getRidesByEmail(
      email,
      this.filterFrom() || undefined,
      this.filterTo() || undefined,
      sortBy
    ).subscribe({
      next: (data) => {
        const sorted = this.applySortDirection(data, sort);
        this.rides.set(sorted);
        this.loading.set(false);
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error loading rides:', err);
        if (err.status === 404) {
          this.error.set('User not found with this email.');
        } else {
          this.error.set('Error loading ride history.');
        }
        this.rides.set([]);
        this.loading.set(false);
        this.cdr.detectChanges();
      }
    });
  }

  private applySortDirection(data: AdminRideHistoryDTO[], sort: SortState): AdminRideHistoryDTO[] {
    if (!sort.column) return data;

    const sorted = [...data];
    const dir = sort.direction === 'asc' ? 1 : -1;
    console.log('Sorting data:', data);

    sorted.sort((a, b) => {
      let cmp = 0;
      switch (sort.column) {
        case 'START_TIME':
          cmp = (a.startTime || '').localeCompare(b.startTime || '');
          break;
        case 'END_TIME':
          cmp = (a.endTime || '').localeCompare(b.endTime || '');
          break;
        case 'START_ADDRESS':
          cmp = (a.startAddress || '').localeCompare(b.startAddress || '');
          break;
        case 'END_ADDRESS':
          cmp = (a.endAddress || '').localeCompare(b.endAddress || '');
          break;
        case 'PRICE':
          cmp = (a.price ?? 0) - (b.price ?? 0);
          break;
        case 'CANCELLED_BY':
          cmp = (a.cancellationSource || '').localeCompare(b.cancellationSource || '');
          break;
        case 'PANIC':
          cmp = (a.panicTriggered ? 1 : 0) - (b.panicTriggered ? 1 : 0);
          break;
        case 'ROUTE':
          const fullRouteA = `${a.startAddress || ''} | ${a.route || ''} | ${a.endAddress || ''}`;
          const fullRouteB = `${b.startAddress || ''} | ${b.route || ''} | ${b.endAddress || ''}`;

          cmp = fullRouteA.localeCompare(fullRouteB, undefined, {
            numeric: true,
            sensitivity: 'base'
          });
          break;
        case 'DATE':
        default:
          cmp = (a.startTime || '').localeCompare(b.startTime || '');
      }
      return cmp * dir;
    });

    return sorted;
  }

  onSort(column: AdminRideSortBy): void {
    const current = this.sortState();
    let direction: SortDirection = 'asc';

    if (current.column === column) {
      direction = current.direction === 'asc' ? 'desc' : 'asc';
    }

    this.sortState.set({ column, direction });
    this.rides.set(this.applySortDirection(this.rides(), { column, direction }));
  }

  getSortIndicator(column: AdminRideSortBy): string {
    const current = this.sortState();
    if (current.column !== column) return '↕';
    return current.direction === 'asc' ? '↑' : '↓';
  }

  toggleFilter(): void {
    this.filterOpen.update(v => !v);
  }

  onFilterApply(range: { from: string; to: string }): void {
    this.filterFrom.set(range.from);
    this.filterTo.set(range.to);
    this.filterOpen.set(false);
    if (this.currentEmail()) {
      this.loadRides();
    }
  }

  onFilterClear(): void {
    this.filterFrom.set('');
    this.filterTo.set('');
    this.filterOpen.set(false);
    if (this.currentEmail()) {
      this.loadRides();
    }
  }

  onRowClick(ride: AdminRideHistoryDTO): void {
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

  formatDateTime(isoString: string | null | undefined): string {
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

  formatRoute(ride: AdminRideHistoryDTO): string {
    // Capitalize for display
    const cap = (s: string) => s && s.length ? s.charAt(0).toUpperCase() + s.slice(1) : '';
    const start = cap((ride.startAddress || '').trim());
    const end = cap((ride.endAddress || '').trim());
    const stations = ride.route
      ? ride.route.split(',').map(s => cap(s.trim())).filter(s => s.length > 0)
      : [];
    let result = start;
    if (stations.length > 0) {
      result += ' → ' + stations.join(' → ');
    }
    result += ' → ' + end;
    return result;
  }

  formatCanceled(ride: AdminRideHistoryDTO): string {
    if (!ride.cancellationSource) return '—';
    return ride.cancellationSource || 'Yes';
  }

  formatPanic(triggered: boolean): string {
    return triggered ? 'Yes' : 'No';
  }
}
