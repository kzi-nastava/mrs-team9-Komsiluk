import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ModalShellComponent } from '../../../../../shared/components/modal-shell/modal-shell.component';
import { LoginRequiredModalService } from '../../../../../shared/components/modal-shell/services/login-required-modal.service';

@Component({
  selector: 'app-login-required-dialog',
  standalone: true,
  imports: [CommonModule, ModalShellComponent],
  templateUrl: './login-required-dialog.component.html',
  styleUrl: './login-required-dialog.component.css',
})
export class LoginRequiredDialogComponent {

  constructor(
    public modal: LoginRequiredModalService,
    private router: Router
  ) {}

  goToLogin() {
    this.modal.confirm();
    this.router.navigate(['/login']);
  }

  close() {
    this.modal.close();
  }
}
