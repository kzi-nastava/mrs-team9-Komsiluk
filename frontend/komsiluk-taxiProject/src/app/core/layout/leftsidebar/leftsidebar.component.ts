import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-leftsidebar',
  standalone: true,
  templateUrl: './leftsidebar.component.html',
  styleUrl: './leftsidebar.component.css',
})
export class LeftSidebarComponent {
  @Input() open = false;
}
