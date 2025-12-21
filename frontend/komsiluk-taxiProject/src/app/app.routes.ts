import { Routes } from '@angular/router';
import { StartMenuComponent } from './features/menu/start-menu/start-menu.component';

import { UserProfilePageComponent } from './features/profile/pages/user-profile-page/user-profile-page.component';
import { ProfileViewComponent } from './features/profile/pages/profile-view/profile-view.component';
import { ProfileEditComponent } from './features/profile/pages/profile-edit/profile-edit.component';
import { ProfileChangePasswordComponent } from './features/profile/pages/profile-change-password/profile-change-password.component';
import { DriverCarViewComponent } from './features/profile/pages/driver-car-view/driver-car-view.component';
import { DriverEditProfileComponent } from './features/profile/pages/driver-edit-profile/driver-edit-profile.component';
import { MessagePageComponent } from './shared/components/message-page/message-page.component';
import { DriverRideHistoryPageComponent } from './features/driver-history/pages/driver-ride-history-page/driver-ride-history-page.component';



export const routes: Routes = [
  { path: '', component: StartMenuComponent },
  { path: 'message/:id', component: MessagePageComponent },
  { path: 'driver-history', component: DriverRideHistoryPageComponent },

  {
    path: 'profile',
    component: UserProfilePageComponent,
    children: [
      { path: '', component: ProfileViewComponent },
      { path: 'edit', component: ProfileEditComponent },
      { path: 'change-password', component: ProfileChangePasswordComponent },
      { path: 'car', component: DriverCarViewComponent },
      { path: 'driver-edit', component: DriverEditProfileComponent }
    ],
  },

  { path: '**', redirectTo: '' }
];
