import { Component, computed, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { GeocodingService, AddressSuggestion } from '../../../../../shared/components/map/services/geocoding.service';
import { RidePlannerService } from '../../../../../shared/components/map/services/ride-planner.service';
import { LoginRequiredModalService } from '../../../../../shared/components/modal-shell/services/login-required-modal.service';
import { ToastService } from '../../../../../shared/components/toast/toast.service';
import { GeoPoint } from '../../../../../shared/components/map/services/routing.service';

@Component({
  selector: 'app-guest-book-ride-panel',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './guest-book-ride-panel.component.html',
  styleUrl: './guest-book-ride-panel.component.css',
})
export class GuestBookRidePanelComponent {

  // input text
  pickupText = signal('');
  destinationText = signal('');

  // suggestions
  pickupSuggestions = signal<AddressSuggestion[]>([]);
  destinationSuggestions = signal<AddressSuggestion[]>([]);

  constructor(
    private geocoding: GeocodingService,
    public ridePlanner: RidePlannerService,
    private loginRequiredModal: LoginRequiredModalService
  ) { }

  /* ---------------- SEARCH ---------------- */

  searchPickup() {
    const q = this.pickupText();
    this.geocoding.search(q).subscribe(list => {
      this.pickupSuggestions.set(list);
    });
  }

  searchDestination() {
    const q = this.destinationText();
    this.geocoding.search(q).subscribe(list => {
      this.destinationSuggestions.set(list);
    });
  }

  /* ---------------- SELECT ---------------- */

  selectPickup(s: AddressSuggestion) {
    this.pickupText.set(s.label);
    this.pickupSuggestions.set([]);

    this.ridePlanner.setPickup(this.toPoint(s));
  }

  selectDestination(s: AddressSuggestion) {
    this.destinationText.set(s.label);
    this.destinationSuggestions.set([]);

    this.ridePlanner.setDestination(this.toPoint(s));
  }

  /* ---------------- STATE ---------------- */

  hasRoute = computed(() => {
    return this.ridePlanner.route() !== null;
  });

  /* ---------------- ACTION ---------------- */

  requestLogin() {
    this.loginRequiredModal.open({
      title: 'Log in required',
      message: 'You need to log in in order to book a ride.',
      confirmText: 'Go to log in',
      cancelText: 'Cancel',
    });
  }

  /* ---------------- UTIL ---------------- */

  private toPoint(s: AddressSuggestion): GeoPoint {
    return {
      lat: s.lat,
      lon: s.lon,
      label: s.label,
    };
  }
}
