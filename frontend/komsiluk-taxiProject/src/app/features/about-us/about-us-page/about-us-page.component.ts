import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-about-us-page',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './about-us-page.component.html',
  styleUrls: ['./about-us-page.component.css'],
})
export class AboutUsPageComponent {
  carSrc = '../../../assets/taxi.png';

  version = '1.0.0';
  lastUpdated = 'May 2025';

  prevent(e: Event) {
    e.preventDefault();
    e.stopPropagation();
  }
}
