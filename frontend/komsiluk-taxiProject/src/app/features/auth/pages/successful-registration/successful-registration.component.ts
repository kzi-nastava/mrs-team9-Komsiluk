import { Component } from '@angular/core';
import { AuthCardComponent } from '../../components/auth-card/auth-card.component';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-successful-registration.component',
  imports: [AuthCardComponent, RouterLink],
  templateUrl: './successful-registration.component.html',
  styleUrl: './successful-registration.component.css',
})
export class SuccessfulRegistrationComponent {

}
