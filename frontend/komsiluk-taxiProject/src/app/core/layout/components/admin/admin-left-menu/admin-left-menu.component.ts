import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AdminBlockUserPanelComponent } from '../block/admin-block-user-panel/admin-block-user-panel.component';

@Component({
  selector: 'app-admin-left-menu',
  standalone: true,
  imports: [CommonModule, AdminBlockUserPanelComponent],
  templateUrl: './admin-left-menu.component.html',
  styleUrl: './admin-left-menu.component.css',
})
export class AdminLeftMenuComponent {
  checkOpen = signal(false);
  blockOpen = signal(false);

  toggle(which: 'check' | 'block') {
    if (which === 'check') this.checkOpen.set(!this.checkOpen());
    if (which === 'block') this.blockOpen.set(!this.blockOpen());
  }
}
