import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ToastService } from '../toast.service';

@Component({
  selector: 'app-toast',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div *ngIf="toast.toast$ | async as t" class="toast" [class.toast--error]="t.type === 'error'">
      <span class="toast__msg">{{ t.message }}</span>
      <button type="button" class="toast__close" (click)="toast.clear()">×</button>
    </div>
  `,
  styleUrls: ['./toast.component.css'],
})
export class ToastComponent {
  constructor(public toast: ToastService) {}
}
