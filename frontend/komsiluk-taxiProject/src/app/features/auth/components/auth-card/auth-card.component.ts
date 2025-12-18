import { Component } from '@angular/core';
import { Input } from '@angular/core';

@Component({
  selector: 'app-auth-card',
  standalone: true,
  templateUrl: './auth-card.component.html',
  styleUrl: './auth-card.component.css',
})
export class AuthCardComponent {
  @Input() title!: string;
  @Input() buttonText = 'Continue';
  @Input() formId!: string;
}