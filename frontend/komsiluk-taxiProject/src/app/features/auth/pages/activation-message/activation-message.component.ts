import { Component } from '@angular/core';
import { AuthCardComponent } from '../../components/auth-card/auth-card.component';
import { ToastService } from '../../../../shared/components/toast/toast.service';
import { RouterLink } from '@angular/router';

@Component({ // mogao bi da se posalje mail preko inputa u buducnosti
  selector: 'app-activation-message',
  imports: [AuthCardComponent, RouterLink],
  templateUrl: './activation-message.component.html',
  styleUrl: './activation-message.component.css',
})
export class ActivationMessageComponent {

  constructor(
    private toast: ToastService
  ) {
    
  }

  resendActivationEmail() {
     this.toast.show('Activation link has been sent to your email.');
  }
}
