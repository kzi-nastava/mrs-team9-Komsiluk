import { Component, signal, ViewChild, inject, effect} from '@angular/core';
import { CommonModule } from '@angular/common';
import { PassengerBookRidePanelComponent } from '../book_ride/passenger-book-ride-panel/passenger-book-ride-panel.component';
import { FavoriteRidesPanelComponent } from '../favorite/favorite-rides-panel/favorite-rides-panel.component';
import { BookRidePrefillService } from '../../../../../shared/components/map/services/book-ride-prefill.service';
import { ScheduledRidesPanelComponent } from '../scheduled/scheduled-rides-panel/scheduled-rides-panel.component';

@Component({
  selector: 'app-passenger-left-menu',
  imports: [CommonModule, PassengerBookRidePanelComponent, FavoriteRidesPanelComponent, ScheduledRidesPanelComponent],
  templateUrl: './passenger-left-menu.component.html',
  styleUrl: './passenger-left-menu.component.css',
})
export class PassengerLeftMenuComponent {
  bookOpen = signal(false);
  favOpen = signal(false);
  schedOpen = signal(false);

  @ViewChild('favPanel') favPanel?: FavoriteRidesPanelComponent;
  @ViewChild(PassengerBookRidePanelComponent) bookPanel?: PassengerBookRidePanelComponent;
  @ViewChild('schedPanel') schedPanel?: ScheduledRidesPanelComponent;

  private prefill = inject(BookRidePrefillService);

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

  toggle(which: 'book' | 'fav' | 'sched') {
    if (which === 'book') this.bookOpen.set(!this.bookOpen());

    if (which === 'fav') {
      const next = !this.favOpen();
      this.favOpen.set(next);
      if (next) setTimeout(() => this.favPanel?.load(), 0);
    }

    if (which === 'sched') {
      const next = !this.schedOpen();
      this.schedOpen.set(next);
      if (next) setTimeout(() => this.schedPanel?.load(), 0);
    }
  }
}
