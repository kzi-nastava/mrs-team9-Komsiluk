import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../../../core/auth/services/auth.service';
import { ToastService } from '../../../../shared/components/toast/toast.service';

@Component({
  selector: 'app-activation',
  template: '',
})
export class ActivationComponent implements OnInit {

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private auth: AuthService,
    private toast: ToastService
  ) {}

  ngOnInit(): void {
    const token = this.route.snapshot.queryParamMap.get('token');

    if (!token) {
      this.toast.show('Invalid activation link.');
      this.router.navigate(['/activation-message']);
      return;
    }

    this.auth.activatePassenger(token).subscribe({
      next: () => {
        this.router.navigate(['/successful-registration']);
      },
      error: () => {
        this.toast.show('Activation link is invalid or expired.');
        this.router.navigate(['/activation-message']);
      }
    });
  }
}
