import { Component, signal, OnInit } from '@angular/core';
import { RouterOutlet, Router, NavigationStart } from '@angular/router';
import { CommonModule, Location } from '@angular/common';

import { NavbarComponent } from './core/layout/navbar/navbar.component';
import { ToastComponent } from './shared/components/toast/toast/toast.component';
import { ToastService } from './shared/components/toast/toast.service';
import { LeftSidebarComponent } from './core/layout/leftsidebar/leftsidebar.component';
import { RightsidebarComponent } from './core/layout/rightsidebar.component/rightsidebar.component';
import { RideHistoryFilterPanelComponent } from './features/driver-history/components/ride-history-filter-panel/ride-history-filter-panel';

import { RideHistoryFilterService } from './features/driver-history/services/driver-history-filter.service';
import { filter } from 'rxjs';

import { ConfirmBookingDialogComponent } from './core/layout/components/passenger/book_ride/confirm-booking-dialog/confirm-booking-dialog.component';
import { ConfirmBookingModalService } from './shared/components/modal-shell/services/confirm-booking-modal.service';
import { AddFavoriteDialogComponent } from './core/layout/components/passenger/favorite/add-favorite-dialog/add-favorite-dialog.component';
import { AddFavoriteModalService } from './shared/components/modal-shell/services/add-favorite-modal.service';
import { FavoriteDetailsModalService } from './shared/components/modal-shell/services/favorite-details-modal.service';
import { FavoriteRouteResponseDTO } from './shared/models/favorite-route.models';
import { FavoriteDetailsDialogComponent } from './core/layout/components/passenger/favorite/favorite-details-dialog/favorite-details-dialog.component';
import { RenameFavoriteDialogComponent } from './core/layout/components/passenger/favorite/rename-favorite-dialog/rename-favorite-dialog.component';
import { DeleteFavoriteDialogComponent } from './core/layout/components/passenger/favorite/delete-favorite-dialog/delete-favorite-dialog.component';
import { RenameFavoriteModalService } from './shared/components/modal-shell/services/rename-favorite-modal.service';
import { DeleteFavoriteModalService } from './shared/components/modal-shell/services/delete-favorite-modal.service';
import { FavoriteRouteService } from './core/layout/components/passenger/favorite/services/favorite-route.service';
import { FavoritesBusService } from './core/layout/components/passenger/favorite/services/favorites-bus.service';
import { ScheduledDetailsDialogComponent } from './core/layout/components/passenger/scheduled/scheduled-details-dialog/scheduled-details-dialog.component';
import { ScheduledDetailsModalService } from './shared/components/modal-shell/services/scheduled-details-modal.service';
import { BlockUserConfirmDialogComponent } from './core/layout/components/admin/block/block-user-confirm-dialog/block-user-confirm-dialog.component';
import { BlockUserConfirmModalService } from './shared/components/modal-shell/services/block-user-confirm-modal.service';
import { AccountBlockedDialogComponent } from './core/layout/components/passenger/book_ride/account-blocked-dialog/account-blocked-dialog.component';
import { AccountBlockedModalService } from './shared/components/modal-shell/services/account-blocked-modal.service';
import { ActivatedRoute, NavigationEnd } from '@angular/router';
import { LeftSidebarCommandService } from './core/layout/components/passenger/services/left-sidebar-command-service.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    RouterOutlet,
    NavbarComponent,
    LeftSidebarComponent,
    RightsidebarComponent,
    ToastComponent,
    RideHistoryFilterPanelComponent,
    ConfirmBookingDialogComponent,
    AddFavoriteDialogComponent,
    FavoriteDetailsDialogComponent,
    RenameFavoriteDialogComponent,
    DeleteFavoriteDialogComponent,
    ScheduledDetailsDialogComponent,
    BlockUserConfirmDialogComponent,
    AccountBlockedDialogComponent
  ],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  protected readonly title = signal('komsiluk-taxiProject');

  isLeftSidebarOpen = false;
  rightOpen = false;

  constructor(public filterSvc: RideHistoryFilterService, private router: Router, public confirmModal: ConfirmBookingModalService,
    public addFavModal: AddFavoriteModalService, public favDetailsModal: FavoriteDetailsModalService, public renameFavModal: RenameFavoriteModalService,
    public deleteFavModal: DeleteFavoriteModalService, public toastService: ToastService, private favoriteApi: FavoriteRouteService, private favBus: FavoritesBusService,
    public schedDetailsModal: ScheduledDetailsModalService, public blockUserModal: BlockUserConfirmModalService, public blockedModal: AccountBlockedModalService,
    private leftCmd: LeftSidebarCommandService, private route: ActivatedRoute, private location: Location) {}

    ngOnInit(): void {
    this.router.events
      .pipe(filter(e => e instanceof NavigationStart))
      .subscribe((e: any) => {
        const url = String(e.url || '');
        const hasLeftCommand = url.includes('lp=1') && url.includes('section=');

        queueMicrotask(() => {
          this.rightOpen = false;

          if (!hasLeftCommand) {
            this.isLeftSidebarOpen = false;
          }
        });
      });

    this.router.events
      .pipe(filter(e => e instanceof NavigationEnd))
      .subscribe(() => {
        const qp = this.route.snapshot.queryParamMap;

        const lp = qp.get('lp');
        const section = qp.get('section');
        const scroll = qp.get('scroll');

        if (lp === '1' && (section === 'book' || section === 'fav' || section === 'sched')) {
          this.isLeftSidebarOpen = true;
          this.rightOpen = false;

          this.leftCmd.emit({ section, scrollId: scroll ?? undefined });

          const cleanPath = this.router.url.split('?')[0];
            this.location.replaceState(cleanPath);
          }
      });
  }

  toggleLeftSidebar() { this.isLeftSidebarOpen = !this.isLeftSidebarOpen; }
  toggleRightSidebar() { this.rightOpen = !this.rightOpen; if (this.rightOpen) this.isLeftSidebarOpen = false; }

  toggleFilter() { this.filterSvc.toggle(); }

  onFilterApplied(range: { from: string; to: string }) {
    this.filterSvc.apply(range);
    this.filterSvc.close();
  }

  onFilterReset() {
    this.filterSvc.reset();
    this.filterSvc.close();
  }

  closeRightSidebar(): void {
  this.rightOpen = false;
  }

  onFavoriteDelete(f: FavoriteRouteResponseDTO) {
    this.favDetailsModal.close();

    this.deleteFavModal.openModal(
      { favoriteId: f.id, title: f.title },
      () => this.confirmDeleteFavorite(f.id)
    );
  }

  private confirmDeleteFavorite(favoriteId: number) {
    this.favoriteApi.deleteFavorite(favoriteId).subscribe({
      next: () => {
        this.toastService.show('Favorite deleted.');
        this.deleteFavModal.close();
        this.favBus.trigger();
      },
      error: (err) => {
        this.toastService.show(err?.error?.message || 'Delete failed.');
      }
    });
  }

  onFavoriteRename(f: FavoriteRouteResponseDTO) {
    this.favDetailsModal.close();

    this.renameFavModal.openModal(
      { favoriteId: f.id, currentTitle: f.title },
      (newTitle: string) => this.confirmRenameFavorite(f.id, newTitle)
    );
  }

  private confirmRenameFavorite(favoriteId: number, newTitle: string) {
    this.favoriteApi.renameFavorite(favoriteId, { title: newTitle }).subscribe({
      next: () => {
        this.toastService.show('Favorite renamed.');
        this.renameFavModal.close();
        this.favBus.trigger();
      },
      error: (err) => {
        this.toastService.show(err?.error?.message || 'Rename failed.');
      }
    });
  }

  onScheduledCancelRide(r: any) {
    console.log('CANCEL SCHEDULED (GUI ONLY):', r);
    this.schedDetailsModal.close();
  }
}
