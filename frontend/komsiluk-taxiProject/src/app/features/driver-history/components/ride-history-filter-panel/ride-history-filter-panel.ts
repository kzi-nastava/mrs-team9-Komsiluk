import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-ride-history-filter-panel',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './ride-history-filter-panel.html',
  styleUrls: ['./ride-history-filter-panel.css'],
})
export class RideHistoryFilterPanelComponent {
  @Output() filterApplied = new EventEmitter<{ from: string; to: string }>();
  @Output() resetClicked = new EventEmitter<void>();

  from = '';
  to = '';

  formatRange(): string {
    return `${this.from || '---'} - ${this.to || '---'}`;
  }

  applyFilter() {
    this.filterApplied.emit({ from: this.from, to: this.to });
  }

reset() {
  this.from = '';
  this.to = '';
  this.filterApplied.emit({ from: '', to: '' });
}
}
