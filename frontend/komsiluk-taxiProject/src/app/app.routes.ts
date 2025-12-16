import { Routes } from '@angular/router';
import { UserProfilePageComponent } from './features/profile/pages/user-profile-page/user-profile-page.component';
import { ProfileViewComponent } from './features/profile/pages/profile_view/profile-view/profile-view.component';
import { ProfileEditComponent } from './features/profile/pages/profile_edit/profile-edit/profile-edit.component';

export const routes: Routes = [
  {
    path: 'profile',
    component: UserProfilePageComponent,
    children: [
      { path: '', component: ProfileViewComponent },
      { path: 'edit', component: ProfileEditComponent },
    ],
  },

  { path: '', redirectTo: 'profile', pathMatch: 'full' },
];
