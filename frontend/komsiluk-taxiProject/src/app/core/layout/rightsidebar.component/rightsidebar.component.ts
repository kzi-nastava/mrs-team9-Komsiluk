import { Component, Input } from '@angular/core';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-rightsidebar',
  standalone: true,
  templateUrl: './rightsidebar.component.html',
  styleUrl: './rightsidebar.component.css',
  imports: [RouterModule]
})
export class RightsidebarComponent {
  @Input() open = false;
}
