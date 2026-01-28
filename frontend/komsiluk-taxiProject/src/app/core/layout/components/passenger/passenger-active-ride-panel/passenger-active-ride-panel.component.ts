import { CommonModule } from "@angular/common";
import { Component, computed, DestroyRef, inject, input, OnInit, signal } from "@angular/core";
import { ReactiveFormsModule } from "@angular/forms";
import { InconsistencyReportModalComponent } from "../../../../../features/ride/components/inconsistency-report-modal/inconsistency-report-modal.component";
import { RidePassengerActiveDTO } from "../../../../../shared/models/ride.models";
import { RideService } from "../book_ride/services/ride.service";
import { interval, of, startWith, switchMap } from "rxjs";
import { takeUntilDestroyed } from "@angular/core/rxjs-interop";
import { MapFacadeService } from "../../../../../shared/components/map/services/map-facade.service";
import { RoutingService } from "../../../../../shared/components/map/services/routing.service";
import { RideProgressService } from "../../../../../shared/components/map/services/ride-progress-service";
import { PanicDialogComponent } from "../../../../../shared/components/panic-dialog/panic-dialog.component";
import { PanicModalService } from "../../../../../shared/components/modal-shell/services/panic-modal-service";

@Component({
  selector: 'app-passenger-active-ride-panel',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, InconsistencyReportModalComponent],
  templateUrl: './passenger-active-ride-panel.component.html',
  styleUrls: ['./passenger-active-ride-panel.component.css'] 
})
export class PassengerActiveRidePanelComponent implements OnInit {
  ride = input.required<RidePassengerActiveDTO>();

  private rideService = inject(RideService);
  private facade = inject(MapFacadeService);
  private routing = inject(RoutingService);
  private progress = inject(RideProgressService);
  private destroyRef = inject(DestroyRef);
  private panicModalSvc = inject(PanicModalService);
  
  showReportModal = signal(false);
  remainingMinutes = signal<number | null>(null);
  showRatingModal = signal(false);
 lastFinishedRideId = signal<number | null>(null);

  stations = computed(() => {
    const currentRide = this.ride();
    return currentRide?.stops || [];
  });

ngOnInit() {
  interval(2000).pipe(
    startWith(0), 
    takeUntilDestroyed(this.destroyRef),
    switchMap(() => {
      const path = this.facade.ridePath(); 
      const driveData = this.facade.driveTo();
      const currentRide = this.ride();

      if (!currentRide) return of(null);
      const targetId = currentRide.driverId;

      if (!path || path.length === 0) {
        console.log("ETA: Waiting for MapComponent to set ridePath in facade...");
        return of(null);
      }

      let currentPos: { lat: number; lon: number };

      if (driveData && driveData.driverId === targetId) {
        currentPos = { lat: driveData.target.lat, lon: driveData.target.lon };
      } else {
        currentPos = { lat: path[0].lat, lon: path[0].lon };
      }

      const currentIndex = this.progress.findClosestRouteIndex(path, currentPos);
      const remainingPath = path.slice(currentIndex);

      if (remainingPath.length < 2) {
        this.remainingMinutes.set(0);
        return of(null);
      }

      return this.routing.route(remainingPath);
    })
  ).subscribe({
    next: (result) => {
      if (result) {
        const mins = Math.round(result.durationSeconds / 60);
        this.remainingMinutes.set(mins);
      }
    },
    error: (err) => {
      console.error("Can't calculate ETA", err);
    }
  });
}
  panic() {
    this.panicModalSvc.openModal(this.ride().rideId);
  }

  reportInconsistency() {
    this.showReportModal.set(true);
  }
}