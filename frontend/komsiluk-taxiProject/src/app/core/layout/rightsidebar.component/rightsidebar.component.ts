import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-rightsidebar',
  standalone: true,
  templateUrl: './rightsidebar.component.html',
  styleUrl: './rightsidebar.component.css',
})
export class RightsidebarComponent {
  @Input() open = false;
}
