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

@Component({
  selector: 'app-passenger-active-ride-panel',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, InconsistencyReportModalComponent],
  templateUrl: './passenger-active-ride-panel.component.html',
  styleUrls: ['./passenger-active-ride-panel.component.css'] // Koristimo isti CSS
})
export class PassengerActiveRidePanelComponent implements OnInit {
  ride = input.required<RidePassengerActiveDTO>();

  private rideService = inject(RideService);
  private facade = inject(MapFacadeService);
  private routing = inject(RoutingService);
  private progress = inject(RideProgressService);
  private destroyRef = inject(DestroyRef);
  
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
    // Pokrećemo odmah (0), pa onda na svake 2 sekunde
    startWith(0), 
    takeUntilDestroyed(this.destroyRef),
    switchMap(() => {
      // 1. Dobavljanje podataka iz fasade i inputa
      const path = this.facade.ridePath(); 
      const driveData = this.facade.driveTo();
      const currentRide = this.ride();

      // Provera da li uopšte imamo podatke o vožnji
      if (!currentRide) return of(null);
      const targetId = currentRide.driverId;

      // 2. Provera putanje (ridePath)
      if (!path || path.length === 0) {
        console.log("ETA: Čekam da MapComponent postavi ridePath u fasadu...");
        return of(null);
      }

      // 3. Određivanje trenutne pozicije vozača
      let currentPos: { lat: number; lon: number };

      // Ako imamo sveže podatke o kretanju baš za tog vozača, koristi njih
      if (driveData && driveData.driverId === targetId) {
        currentPos = { lat: driveData.target.lat, lon: driveData.target.lon };
      } else {
        // Fallback: Ako polling još nije stigao, koristi prvu tačku putanje rute
        currentPos = { lat: path[0].lat, lon: path[0].lon };
      }

      // 4. Kalkulacija preostalog dela rute preko RideProgressService
      const currentIndex = this.progress.findClosestRouteIndex(path, currentPos);
      const remainingPath = path.slice(currentIndex);

      // Ako je vozač stigao na kraj ili je preostala samo jedna tačka
      if (remainingPath.length < 2) {
        this.remainingMinutes.set(0);
        return of(null);
      }

      // 5. Pozivanje OSRM servisa za dobijanje sekundi preostale putanje
      return this.routing.route(remainingPath);
    })
  ).subscribe({
    next: (result) => {
      if (result) {
        // Pretvaranje sekundi u minute
        const mins = Math.round(result.durationSeconds / 60);
        this.remainingMinutes.set(mins);
      }
    },
    error: (err) => {
      console.error("Greška pri računanju ETA (Passenger Panel):", err);
    }
  });
}
  panic() {
    if (confirm('Are you sure you want to activate PANIC mode?')) {
      this.rideService.activatePanicButton(this.ride().rideId);
    }
  }

  reportInconsistency() {
    this.showReportModal.set(true);
  }
}