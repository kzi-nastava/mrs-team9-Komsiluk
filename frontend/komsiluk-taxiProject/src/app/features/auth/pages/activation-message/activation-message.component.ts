import { Component } from '@angular/core';
import { AuthCardComponent } from '../../components/auth-card/auth-card.component';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ToastService } from '../../../../shared/components/toast/toast.service';
import { AuthService } from '../../../../core/auth/services/auth.service';

@Component({
  selector: 'app-activation-message',
  imports: [AuthCardComponent, RouterLink],
  templateUrl: './activation-message.component.html',
  styleUrl: './activation-message.component.css',
})
export class ActivationMessageComponent {

  email: string | null = null;
  loading = false;

  constructor(
    private route: ActivatedRoute,
    private auth: AuthService,
    private toast: ToastService
  ) {
    this.email = this.route.snapshot.queryParamMap.get('email');
  }

  resendActivationEmail() {
    if (!this.email || this.loading) {
      return;
    }

    this.loading = true;

    this.auth.resendActivation(this.email).subscribe({
      next: () => {
        this.toast.show('If the email exists, the activation link was sent.');
        this.loading = false;
      },
      error: (err: { status: number; }) => {
        if (err?.status === 429) {
          this.toast.show('Activation email already sent. Please check your inbox.');
        } else if(err?.status === 400) {
          this.toast.show('Invalid email address.');
        }
          else {
          this.toast.show('Something went wrong. Please try again later.');
        }
        this.loading = false;
      }
    });
  }
}
