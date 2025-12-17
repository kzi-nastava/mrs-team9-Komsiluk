import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { ToastService } from '../../../../shared/components/toast/toast.service';

@Component({
  selector: 'app-profile-edit',
  imports: [],
  templateUrl: './profile-edit.component.html',
  styleUrl: './profile-edit.component.css',
})
export class ProfileEditComponent {
  constructor(private toast: ToastService, private router: Router) {}

  close() {
    this.router.navigate(['/profile']);
  }

  save() {
    this.toast.show('Profile updated successfully!');
    this.router.navigate(['/profile']);
  }
}