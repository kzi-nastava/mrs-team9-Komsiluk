import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PassengerBookRidePanelComponent } from '../book_ride/passenger-book-ride-panel/passenger-book-ride-panel.component';

@Component({
  selector: 'app-passenger-left-menu',
  imports: [CommonModule, PassengerBookRidePanelComponent],
  templateUrl: './passenger-left-menu.component.html',
  styleUrl: './passenger-left-menu.component.css',
})
export class PassengerLeftMenuComponent {
  bookOpen = signal(false);
  favOpen = signal(false);
  schedOpen = signal(false);

  toggle(which: 'book' | 'fav' | 'sched') {
    if (which === 'book') this.bookOpen.set(!this.bookOpen());
    if (which === 'fav') this.favOpen.set(!this.favOpen());
    if (which === 'sched') this.schedOpen.set(!this.schedOpen());
  }
}
