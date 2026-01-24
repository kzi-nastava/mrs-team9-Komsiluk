import { Routes } from '@angular/router';
import { StartMenuComponent } from './features/menu/start-menu/start-menu.component';

import { UserProfilePageComponent } from './features/profile/pages/user-profile-page/user-profile-page.component';
import { ProfileViewComponent } from './features/profile/pages/profile-view/profile-view.component';
import { ProfileEditComponent } from './features/profile/pages/profile-edit/profile-edit.component';
import { ProfileChangePasswordComponent } from './features/profile/pages/profile-change-password/profile-change-password.component';
import { DriverCarViewComponent } from './features/profile/pages/driver-car-view/driver-car-view.component';
import { DriverEditProfileComponent } from './features/profile/pages/driver-edit-profile/driver-edit-profile.component';
import { UsageReportsPageComponent } from './features/usage-report/pages/usage-reports-page/usage-reports-page.component';
import { AboutUsPageComponent } from './features/about-us/about-us-page/about-us-page.component';

import { MessagePageComponent } from './shared/components/message-page/message-page.component';
import { DriverRideHistoryPageComponent } from './features/driver-history/pages/driver-ride-history-page/driver-ride-history-page.component';

import { authGuard } from './core/auth/guards/auth.guard';
import { roleGuard } from './core/auth/guards/role.guard';
import { UserRole } from './core/auth/services/auth.service';
import { AdminDriverChangeRequestsPageComponent } from './features/approve-edit/admin-driver-change-requests-page/admin-driver-change-requests-page.component';

import { AdminDriverRegistrationPageComponent } from './features/add-driver/admin-driver-registration-page/admin-driver-registration-page.component';

export const routes: Routes = [

  // ===== PUBLIC =====
  { path: '', component: StartMenuComponent },
  { path: 'message/:id', component: MessagePageComponent },

  // ===== DRIVER =====
  {
    path: 'driver-history',
    component: DriverRideHistoryPageComponent,
    canActivate: [authGuard, roleGuard],
    data: { roles: [UserRole.DRIVER] }
  },

  // ===== PROFILE (ulogovan korisnik) =====
  {
    path: 'profile',
    component: UserProfilePageComponent,
    canActivate: [authGuard],
    children: [
      { path: '', component: ProfileViewComponent },
      { path: 'edit', component: ProfileEditComponent },
      { path: 'change-password', component: ProfileChangePasswordComponent },

      // DRIVER-only delovi profila
      {
        path: 'car',
        component: DriverCarViewComponent,
        canActivate: [roleGuard],
        data: { roles: [UserRole.DRIVER] }
      },
      {
        path: 'driver-edit',
        component: DriverEditProfileComponent,
        canActivate: [roleGuard],
        data: { roles: [UserRole.DRIVER] }
      }
    ],
  },

  // ===== USAGE REPORT =====
  {
    path: 'usage-report',
    component: UsageReportsPageComponent,
    canActivate: [authGuard],
  },

  // ===== ADMIN =====
  { 
    path: 'driver-change-requests',
    component: AdminDriverChangeRequestsPageComponent,
    canActivate: [authGuard, roleGuard],
    data: { roles: [UserRole.ADMIN] }
  },
  {
    path: 'admin/add-driver',
    component: AdminDriverRegistrationPageComponent,
    canActivate: [authGuard, roleGuard],
    data: { roles: [UserRole.ADMIN] }
  },

  // ===== AUTH (PUBLIC, lazy) =====
  {
    path: '',
    loadChildren: () =>
      import('./features/auth/auth.routes')
        .then(m => m.AUTH_ROUTES),
  },

  { path: 'about', component: AboutUsPageComponent },

  // ===== FALLBACK =====
  { path: '**', redirectTo: '' }
];
