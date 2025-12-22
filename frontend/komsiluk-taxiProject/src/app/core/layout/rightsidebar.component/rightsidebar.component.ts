import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-rightsidebar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './rightsidebar.component.html',
  styleUrls: ['./rightsidebar.component.css'],
})
export class RightsidebarComponent {
  @Input() open = false;
  isLoggedIn: boolean = false;
  isDriverHistory: boolean = false;

  constructor(private router: Router) {
    // Ovaj kod prati promene u URL-u
    this.router.events.pipe(filter(e => e instanceof NavigationEnd)).subscribe(() => {
      this.isDriverHistory = this.router.url.startsWith('/driver-history');
      // Postavljamo isLoggedIn vrednost prema URL-u
      this.isLoggedIn = this.isDriverHistory; // Pretpostavljamo da ako je URL za driver-history, korisnik je ulogovan
    });
  }
}
