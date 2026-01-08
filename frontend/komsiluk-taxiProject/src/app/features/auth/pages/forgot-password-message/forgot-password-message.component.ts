import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthCardComponent } from '../../components/auth-card/auth-card.component';

@Component({
  selector: 'app-forgot-password-message',
  imports: [AuthCardComponent],
  templateUrl: './forgot-password-message.component.html',
  styleUrl: './forgot-password-message.component.css',
})
export class ForgotPasswordMessageComponent {

  email: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.email = this.route.snapshot.queryParamMap.get('email');
  }

  close() {
    this.router.navigate(['/']);
  }
}
