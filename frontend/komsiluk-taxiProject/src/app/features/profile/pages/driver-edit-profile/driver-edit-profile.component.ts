import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-driver-edit-profile',
  imports: [],
  templateUrl: './driver-edit-profile.component.html',
  styleUrl: './driver-edit-profile.component.css',
})
export class DriverEditProfileComponent {
  constructor(private router: Router) {}

  close() {
    this.router.navigate(['/profile']);
  }

  save() {
    
  }
}
