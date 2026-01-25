import { Component, ElementRef, EventEmitter, Input, Output, Renderer2, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ToastService } from '../../../../../shared/components/toast/toast.service';
import { finalize } from 'rxjs';
import { RideService } from '../../../../../core/layout/components/passenger/book_ride/services/ride.service';

@Component({
  selector: 'app-inconsistency-report-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './inconsistency-report-modal.component.html',
  styleUrls: ['./inconsistency-report-modal.component.css'], // Koristi isti CSS kao rating modal
})
export class InconsistencyReportModalComponent {
  @Input({ required: true }) rideId!: number;
  @Output() close = new EventEmitter<void>();
  @Output() submitted = new EventEmitter<void>();

  private api = inject(RideService);
  private toast = inject(ToastService);

  message: string = '';
  loading: boolean = false;

  private renderer = inject(Renderer2);
  private elementRef = inject(ElementRef);

  submit() {
    if (!this.message.trim()) return;

    this.loading = true;
    this.api.reportInconsistency(this.rideId, this.message)
      .pipe(finalize(() => this.loading = false))
      .subscribe({
        next: () => {
          this.toast.show('Inconsistency reported successfully.');
          this.submitted.emit();
          this.close.emit();
        },
        error: () => {
          this.toast.show('Failed to send report.');
        }
      });
  }

  ngOnInit() {
    // Pomera ceo modal na kraj body-ja čim se kreira
    this.renderer.appendChild(document.body, this.elementRef.nativeElement);
  }

  ngOnDestroy() {
    // Uklanja ga iz body-ja kada Angular uništi komponentu
    this.renderer.removeChild(document.body, this.elementRef.nativeElement);
  }
}