import { Routes } from '@angular/router';
import { UserProfilePageComponent } from './features/profile/pages/user-profile-page/user-profile-page.component';

export const routes: Routes = [
  { path: 'profile', component: UserProfilePageComponent },

  // temporary redirect to profile for easy testing
  { path: '', redirectTo: 'profile', pathMatch: 'full' },
];