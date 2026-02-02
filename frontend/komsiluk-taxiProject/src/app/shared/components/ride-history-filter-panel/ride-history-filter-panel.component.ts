import { Component, EventEmitter, Output, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-ride-history-filter-panel',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './ride-history-filter-panel.component.html',
  styleUrls: ['./ride-history-filter-panel.component.css'],
})
export class RideHistoryFilterPanelComponent implements OnInit {
  @Input() initialFrom = '';
  @Input() initialTo = '';
  @Output() filterApplied = new EventEmitter<{ from: string; to: string }>();
  @Output() resetClicked = new EventEmitter<void>();

  from = '';
  to = '';

  ngOnInit(): void {
    this.from = this.initialFrom;
    this.to = this.initialTo;
  }

  formatRange(): string {
    return `${this.from || '---'} : ${this.to || '---'}`;
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
