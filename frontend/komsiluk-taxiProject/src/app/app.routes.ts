import { Routes } from '@angular/router';
import { StartMenuComponent } from './features/menu/start-menu/start-menu.component';

import { UserProfilePageComponent } from './features/profile/pages/user-profile-page/user-profile-page.component';
import { ProfileViewComponent } from './features/profile/pages/profile-view/profile-view.component';
import { ProfileEditComponent } from './features/profile/pages/profile-edit/profile-edit.component';
import { ProfileChangePasswordComponent } from './features/profile/pages/profile-change-password/profile-change-password.component';
import { DriverCarViewComponent } from './features/profile/pages/driver-car-view/driver-car-view.component';
import { DriverEditProfileComponent } from './features/profile/pages/driver-edit-profile/driver-edit-profile.component';

export const routes: Routes = [
  { path: '', component: StartMenuComponent },

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
