import { Component } from '@angular/core';
import { AuthCardComponent } from '../../components/auth-card/auth-card.component';
import { ToastService } from '../../../../shared/components/toast/toast.service';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-recovery-activation',
  imports: [AuthCardComponent, RouterLink],
  templateUrl: './recovery-activation.html',
  styleUrl: './recovery-activation.css',
})
export class RecoveryActivation {

  constructor(
    private toast: ToastService
  ) {
    
  }

  resendActivationEmail() {
     this.toast.show('Activation link has been resent to your email.');
  }
}
