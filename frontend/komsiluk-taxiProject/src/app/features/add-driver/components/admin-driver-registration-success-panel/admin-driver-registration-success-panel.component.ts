import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-admin-driver-registration-success-panel',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './admin-driver-registration-success-panel.component.html',
  styleUrls: ['./admin-driver-registration-success-panel.component.css'],
})
export class AdminDriverRegistrationSuccessPanelComponent {
  @Output() done = new EventEmitter<void>();
}
