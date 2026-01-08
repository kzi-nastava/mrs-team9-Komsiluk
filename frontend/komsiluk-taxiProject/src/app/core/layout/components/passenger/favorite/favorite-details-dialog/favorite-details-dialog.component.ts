import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ModalShellComponent } from '../../../../../../shared/components/modal-shell/modal-shell.component';
import { FavoriteRouteResponseDTO } from '../../../../../../shared/models/favorite-route.models'; // prilagodi putanju
import { BookRidePrefillService } from '../../../../../../shared/components/map/services/book-ride-prefill.service';

@Component({
  selector: 'app-favorite-details-dialog',
  standalone: true,
  imports: [CommonModule, ModalShellComponent],
  templateUrl: './favorite-details-dialog.component.html',
  styleUrl: './favorite-details-dialog.component.css',
})
export class FavoriteDetailsDialogComponent {

  constructor(private prefill: BookRidePrefillService) {}

  @Input() open = false;
  @Input() favorite: FavoriteRouteResponseDTO | null = null;
  @Input() passengerEmails: string[] = [];

  @Output() cancel = new EventEmitter<void>();
  @Output() book = new EventEmitter<FavoriteRouteResponseDTO>();
  @Output() delete = new EventEmitter<FavoriteRouteResponseDTO>();
  @Output() rename = new EventEmitter<FavoriteRouteResponseDTO>();

  get stopsList(): string[] {
    const s = this.favorite?.stops ?? [];
    return Array.isArray(s) ? s : [];
  }

  get usersCount(): number {
    return this.favorite?.passengerIds?.length ?? 0;
  }

  get stationsCount(): number {
    return this.stopsList.length;
  }

  onBook() {
    const f = this.favorite;
    if (!f) return;

    this.prefill.request({
      favorite: f, passengerEmails: this.passengerEmails
    });

    this.cancel.emit();
  }

  onDelete() {
    const f = this.favorite;
    if (!f) return;
    this.delete.emit(f);
  }

  onRename() {
    const f = this.favorite;
    if (!f) return;
    this.rename.emit(f);
  }
}
