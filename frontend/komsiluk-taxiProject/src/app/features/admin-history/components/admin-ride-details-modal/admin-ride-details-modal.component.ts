import { CommonModule } from '@angular/common';
import { Component, EventEmitter, HostListener, Input, Output } from '@angular/core';

import { RideDetailsMapComponent } from '../../../../shared/components/ride-details-map/ride-details-map.component';
import { AdminRideDetailsDTO } from '../../services/admin-ride-history-api.service';

@Component({
  selector: 'app-admin-ride-details-modal',
  standalone: true,
  imports: [CommonModule, RideDetailsMapComponent],
  templateUrl: './admin-ride-details-modal.component.html',
  styleUrls: ['./admin-ride-details-modal.component.css'],
})
export class AdminRideDetailsModalComponent {
  @Input() details: AdminRideDetailsDTO | null = null;
  @Input() loading = false;
  @Output() close = new EventEmitter<void>();

  private readonly IMG_BASE = 'http://localhost:8081';

  onBackdropClick(): void {
    this.close.emit();
  }

  onDialogClick(e: MouseEvent): void {
    e.stopPropagation();
  }

  @HostListener('document:keydown.escape')
  onEsc(): void {
    this.close.emit();
  }

  get allLocations(): string[] {
    if (!this.details?.route) return [];
    const street = (addr: string) => typeof addr === 'string' ? addr.split(',')[0].trim() : '';
    const stopsArr = this.stopsArray;
    return [
      street(this.details.route.startAddress),
      ...stopsArr,
      street(this.details.route.endAddress)
    ];
  }

  get stopsArray(): string[] {
    if (!this.details?.route) return [];
    const street = (addr: string) => typeof addr === 'string' ? addr.split(',')[0].trim() : '';
    const rawStops: any = this.details.route.stops;
    if (rawStops == null) {
      return [];
    } else if (Array.isArray(rawStops)) {
      return rawStops.filter((s: string) => !!s).map(street);
    } else if (typeof rawStops === 'string') {
      return rawStops.split('|').filter((s: string) => !!s.trim()).map((s: string) => street(s));
    }
    return [];
  }

  get driverFullName(): string {
    if (!this.details?.driver) return 'N/A';
    return `${this.details.driver.firstName} ${this.details.driver.lastName}`;
  }

  get driverProfilePicture(): string | null {
    const url = this.details?.driver?.profileImageUrl?.trim();
    if (!url) return null;
    return `${this.IMG_BASE}${url}`;
  }

  get driverRating(): string {
    if (!this.details?.driver?.averageRating) return 'N/A';
    return `${this.details.driver.averageRating.toFixed(1)} ★`;
  }

  get passengerEmailsList(): string[] {
    return this.details?.passengerEmails ?? [];
  }

  isCanceled(): boolean {
    return this.details?.cancellationSource != null;
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

  fmtRating(v?: number | null): string {
    return v == null ? 'N/A' : `${v} ★`;
  }

  fmtText(v?: string | null): string {
    return v && v.trim().length ? v : 'N/A';
  }

  // === Actions (disabled for admin - placeholder only) ===

  onOrderAgain(): void {
    // Not implemented for admin view
  }

  onSaveAsFavorite(): void {
    // Not implemented for admin view
  }
}
