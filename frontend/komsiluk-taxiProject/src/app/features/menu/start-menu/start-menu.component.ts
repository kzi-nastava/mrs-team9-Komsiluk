import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { UserModeService } from '../../../shared/util/user_mode/user-mode.service';

@Component({
  selector: 'app-start-menu',
  imports: [],
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