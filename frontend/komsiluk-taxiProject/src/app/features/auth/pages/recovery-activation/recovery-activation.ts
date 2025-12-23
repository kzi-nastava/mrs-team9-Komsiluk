import { Component } from '@angular/core';
import { AuthCardComponent } from '../../components/auth-card/auth-card.component';
import { ToastService } from '../../../../shared/components/toast/toast.service';

@Component({
  selector: 'app-recovery-activation',
  imports: [AuthCardComponent],
  templateUrl: './recovery-activation.html',
  styleUrl: './recovery-activation.css',
})
export class RecoveryActivation {

  constructor(
    private toast: ToastService
  ) {
    
  }

  save() {
     this.toast.show('Activation link has been resent to your email.');
  }
}
