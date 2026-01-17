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



  pickupSuggestions = signal<AddressSuggestion[]>([]);
  destinationSuggestions = signal<AddressSuggestion[]>([]);

  constructor(
    private geocoding: GeocodingService,
    public ridePlanner: RidePlannerService,
    private loginRequiredModal: LoginRequiredModalService,
    private toast: ToastService
  ) { }



  searchPickup() {
    const q = this.ridePlanner.pickupText();
    this.geocoding.search(q).subscribe(list => {
      this.pickupSuggestions.set(list);
    });
  }

  searchDestination() {
    const q = this.ridePlanner.destinationText();
    this.geocoding.search(q).subscribe(list => {
      this.destinationSuggestions.set(list);
    });
  }



  selectPickup(s: AddressSuggestion) {
    this.ridePlanner.setPickupText(s.label);
    this.pickupSuggestions.set([]);

    this.ridePlanner.setPickup(this.toPoint(s));
  }

  selectDestination(s: AddressSuggestion) {
    this.ridePlanner.setDestinationText(s.label);
    this.destinationSuggestions.set([]);

    this.ridePlanner.setDestination(this.toPoint(s));
  }



  hasRoute = computed(() => this.ridePlanner.route() !== null);



  requestLogin() {
    this.loginRequiredModal.open({
      title: 'Log in required',
      message: 'You need to log in in order to book a ride.',
      confirmText: 'Go to log in',
      cancelText: 'Cancel',
    });
  }

  onBookClick() {
    if (!this.ridePlanner.pickup() || !this.ridePlanner.destination()) {
      this.toast.show('Please select both pickup and destination locations.');
      return;
    }

    this.requestLogin();
  }




  private toPoint(s: AddressSuggestion): GeoPoint {
    return {
      lat: s.lat,
      lon: s.lon,
      label: s.label,
    };
  }
}
