import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';

import { GuestBookRidePanelComponent } from '../guest-book-ride-panel/guest-book-ride-panel.component';

@Component({
  selector: 'app-guest-left-menu',
  standalone: true,
  imports: [
    CommonModule,
    GuestBookRidePanelComponent,
  ],
  templateUrl: './guest-left-menu.component.html',
  styleUrl: './guest-left-menu.component.css',
})
export class GuestLeftMenuComponent {
  bookOpen = signal(true);

  toggle() {
    this.bookOpen.update(v => !v);
  }
}
