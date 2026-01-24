import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { AuthCardComponent } from '../../../features/auth/components/auth-card/auth-card.component';
import { ToastService } from '../toast/toast.service';
import { AuthService, UserRole } from '../../../core/auth/services/auth.service';

import { MESSAGE_REGISTRY, MessageAction, MessageId } from './message-registry';
import { RidePlannerService } from '../map/services/ride-planner.service';

import { DriverService } from '../../../core/layout/components/driver/services/driver.service';
import { DriverRuntimeStateService } from '../../../core/layout/components/driver/services/driver-runtime-state.service';

@Component({
  selector: 'app-message-page',
  imports: [AuthCardComponent],
  templateUrl: './message-page.component.html',
  styleUrl: './message-page.component.css',
})
export class MessagePageComponent {
  title = '';
  description = '';
  actions: MessageAction[] = [];

  constructor(
    private route: ActivatedRoute,
    private ridePlanner: RidePlannerService,
    private router: Router,
    private auth: AuthService,
    private toast: ToastService,
    private driverApi: DriverService,
    private driverState: DriverRuntimeStateService
  ) {
    const id = this.route.snapshot.paramMap.get('id') as MessageId | null;

    if (!id || !(id in MESSAGE_REGISTRY)) {
      this.router.navigate(['/']);
      return;
    }

    const msg = MESSAGE_REGISTRY[id];
    this.title = msg.title;
    this.description = msg.description;
    this.actions = msg.actions;
  }

  run(a: MessageAction) {
    if (a.kind === 'navigate') {
      this.router.navigateByUrl(a.url);
      return;
    }

    const id = Number(this.auth.userId());
    const role = this.auth.userRole();
    const s = this.driverState.status();

    const shouldGoInactive = role === UserRole.DRIVER && id && (s === 'ACTIVE' || s === 'IN_RIDE');

    if (shouldGoInactive) {
      this.driverApi.updateStatus(id, 'INACTIVE').subscribe({
        next: () => {
          this.driverState.setStatus('INACTIVE');
          this.auth.logout();
          if (a.toast) this.toast.show(a.toast);
            this.router.navigateByUrl(a.urlAfter);
            this.ridePlanner.reset();
        },
        error: () => {
          this.auth.logout();
          if (a.toast) this.toast.show(a.toast);
          this.router.navigateByUrl(a.urlAfter);
          this.ridePlanner.reset();
        }
      });
      return;
    }

    this.auth.logout();
    if (a.toast) this.toast.show(a.toast);
    this.router.navigateByUrl(a.urlAfter);
    this.ridePlanner.reset();
  }
}
