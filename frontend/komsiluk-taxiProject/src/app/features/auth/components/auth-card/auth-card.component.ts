import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-auth-card',
  standalone: true,
  templateUrl: './auth-card.component.html',
  styleUrl: './auth-card.component.css',
})
export class AuthCardComponent {

  constructor() {}

  @Input() title!: string;
}