import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { UserModeService } from '../../../shared/components/user_mode/user-mode.service';
import { MapComponent } from '../../../shared/components/map/map';

@Component({
  selector: 'app-start-menu',
  imports: [MapComponent],
  templateUrl: './start-menu.component.html',
  styleUrl: './start-menu.component.css',
})
export class StartMenuComponent {
  constructor(private router: Router, private mode: UserModeService) {}

  openUser() {
    this.mode.setMode('user');
    this.router.navigate(['/profile']);
  }

  openDriver() {
    this.mode.setMode('driver');
    this.router.navigate(['/profile']);
  }
}