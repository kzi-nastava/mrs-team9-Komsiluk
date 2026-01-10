import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

type DiffRow = {
  key: string;
  label: string;
  current: string;
  requested: string;
};

@Component({
  selector: 'app-driver-change-request-review-dialog',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './driver-change-request-review-dialog.component.html',
  styleUrls: ['./driver-change-request-review-dialog.component.css']
})
export class DriverChangeRequestReviewDialogComponent {

  constructor() { }

  @Input() open = false;
  @Input() driverEmail = '';
  @Input() requestedAt = '';
  @Input() rows: DiffRow[] = [];

  @Output() close = new EventEmitter<void>();
  @Output() approve = new EventEmitter<void>();
  @Output() reject = new EventEmitter<void>();
}
