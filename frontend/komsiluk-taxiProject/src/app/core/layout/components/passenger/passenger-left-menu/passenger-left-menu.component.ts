import { Component, AfterViewInit, OnInit, OnDestroy, signal, ViewChild, inject, effect } from '@angular/core';
import { CommonModule } from '@angular/common';
import { interval, startWith, switchMap, forkJoin, of, map, Subscription } from 'rxjs';
import { PassengerBookRidePanelComponent } from '../book_ride/passenger-book-ride-panel/passenger-book-ride-panel.component';
import { FavoriteRidesPanelComponent } from '../favorite/favorite-rides-panel/favorite-rides-panel.component';
import { ScheduledRidesPanelComponent } from '../scheduled/scheduled-rides-panel/scheduled-rides-panel.component';
import { PassengerActiveRidePanelComponent } from "../passenger-active-ride-panel/passenger-active-ride-panel.component";
import { RideService } from '../book_ride/services/ride.service';
import { MapFacadeService } from '../../../../../shared/components/map/services/map-facade.service';
import { GeocodingService } from '../../../../../shared/components/map/services/geocoding.service';
import { BookRidePrefillService } from '../../../../../shared/components/map/services/book-ride-prefill.service';
import { LeftSidebarCommandService } from '../services/left-sidebar-command-service.service';
import { RidePassengerActiveDTO } from '../../../../../shared/models/ride.models';
import { AuthService } from '../../../../auth/services/auth.service';
import { DriverRatingModalComponent } from '../../../../../features/ride/components/driver-rating-modal/driver-raitng-modal';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-passenger-left-menu',
  standalone: true,
  imports: [
    CommonModule, 
    PassengerBookRidePanelComponent, 
    FavoriteRidesPanelComponent, 
    ScheduledRidesPanelComponent, 
    PassengerActiveRidePanelComponent,
    DriverRatingModalComponent
  ],
  templateUrl: './passenger-left-menu.component.html',
  styleUrl: './passenger-left-menu.component.css',
})
export class PassengerLeftMenuComponent implements AfterViewInit, OnInit, OnDestroy {
  bookOpen = signal(false);
  favOpen = signal(false);
  schedOpen = signal(false);
  activeRide = signal<RidePassengerActiveDTO | null>(null);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  @ViewChild('favPanel') favPanel?: FavoriteRidesPanelComponent;
  @ViewChild(PassengerBookRidePanelComponent) bookPanel?: PassengerBookRidePanelComponent;
  @ViewChild('schedPanel') schedPanel?: ScheduledRidesPanelComponent;

  private pollingSub?: Subscription;
  private rideService = inject(RideService);
  private mapFacade = inject(MapFacadeService);
  private geoService = inject(GeocodingService);
  private prefill = inject(BookRidePrefillService);
  private leftCmd = inject(LeftSidebarCommandService);

  public authService = inject(AuthService); 

  showRatingModal = signal(false);
  lastFinishedRideId = signal<number | null>(null);

  constructor() {
    effect(() => {
      const data = this.prefill.pending();
      if (!data) return;
      this.bookOpen.set(true);
      setTimeout(() => {
        this.bookPanel?.applyPrefillFromFavorite(data.favorite, data.passengerEmails);
        this.bookPanel?.scrollIntoView();
        this.prefill.clear();
      }, 0);
    });
  }

ngOnInit(): void {
  this.route.queryParams.subscribe(params => {
    const rideIdFromUrl = params['rateRideId'];
    
    if (rideIdFromUrl) {
      this.lastFinishedRideId.set(Number(rideIdFromUrl));
      this.showRatingModal.set(true);
      
      this.router.navigate([], {
        queryParams: { rateRideId: null },
        queryParamsHandling: 'merge',
        replaceUrl: true 
      });
    }
  });

  this.startPolling();
}

  private startPolling() {
    this.pollingSub = interval(2000).pipe(
      startWith(0),
      switchMap(() => this.rideService.getActiveRideForPassenger()),
      switchMap(ride => {
        if (!ride) return of({ ride: null, waypoints: [] });

        const addresses = [ride.startAddress, ...(ride.stops || []), ride.endAddress];
        const requests = addresses.map(addr => this.geoService.lookupOne(addr));

        return forkJoin(requests).pipe(
          map(results => ({
            ride,
            waypoints: results.filter(r => !!r).map(r => ({ lat: +r!.lat, lon: +r!.lon, label: r!.label }))
          }))
        );
      })
    ).subscribe({
      next: ({ ride, waypoints }) => {
        const prev = this.activeRide();
        
        if (prev && !ride) {
          this.lastFinishedRideId.set(prev.rideId);
          this.showRatingModal.set(true);
          this.mapFacade.clearFocusRide();
        }
        this.activeRide.set(ride);

        if (ride) {
          if (!prev || prev.rideId !== ride.rideId) {
            if (waypoints.length >= 2) {
              this.mapFacade.setFocusRide(ride.driverId ?? 0, waypoints);
            }
          }
        }
      },
      error: (err) => {
        console.error("Pooling error", err);
        this.activeRide.set(null);
        this.mapFacade.clearFocusRide();
      }
    });
  }

  toggle(which: 'book' | 'fav' | 'sched') {
    if (which === 'book') this.bookOpen.set(!this.bookOpen());
    if (which === 'fav') {
      this.favOpen.update(v => !v);
      if (this.favOpen()) setTimeout(() => this.favPanel?.load(), 0);
    }
    if (which === 'sched') {
      this.schedOpen.update(v => !v);
      if (this.schedOpen()) setTimeout(() => this.schedPanel?.load(), 0);
    }
  }

  ngAfterViewInit(): void {
    this.leftCmd.cmd$.subscribe(cmd => {
      this.openSection(cmd.section);
      if (cmd.scrollId) {
        setTimeout(() => {
          document.getElementById(cmd.scrollId!)?.scrollIntoView({ behavior: 'smooth', block: 'center' });
        }, 0);
      }
    });
  }

  private openSection(section: 'book' | 'fav' | 'sched') {
    this.bookOpen.set(section === 'book');
    this.favOpen.set(section === 'fav');
    this.schedOpen.set(section === 'sched');
  }

  ngOnDestroy(): void {
    this.pollingSub?.unsubscribe();
  }
}