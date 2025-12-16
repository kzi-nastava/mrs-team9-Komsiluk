import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { ToastService } from '../../../../../shared/components/toast/toast.service';

@Component({
  selector: 'app-profile-change-password',
  imports: [],
  templateUrl: './profile-change-password.component.html',
  styleUrl: './profile-change-password.component.css',
})
export class ProfileChangePasswordComponent {
  constructor(private toast: ToastService, private router: Router) {}

  close() {
    this.router.navigate(['/profile']);
  }

  save() {
    this.toast.show('Password changed successfully!');
    this.router.navigate(['/profile']);
  }
}
