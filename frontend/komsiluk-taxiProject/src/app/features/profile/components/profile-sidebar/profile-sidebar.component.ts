import { Component, Input } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-profile-sidebar',
  imports: [RouterLink],
  templateUrl: './profile-sidebar.component.html',
  styleUrl: './profile-sidebar.component.css',
})
export class ProfileSidebarComponent {
  @Input() isDriver = false;
  @Input() activeToday: string = '5h 23m';
}
