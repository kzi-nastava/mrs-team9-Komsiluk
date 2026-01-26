import { Component, ElementRef, EventEmitter, Input, Output, Renderer2, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-driver-rating-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
    templateUrl: './driver-rating-modal.html',
    styleUrls: ['./driver-rating-modal.css'],
})
export class DriverRatingModalComponent {
  @Input({ required: true }) rideId!: number;
  @Input({ required: true }) raterId!: number;
  @Output() close = new EventEmitter<void>();
  @Output() submitted = new EventEmitter<any>();

  private http = inject(HttpClient);
  private renderer = inject(Renderer2);
  private elementRef = inject(ElementRef);
  driverGrade: number = 0;
  vehicleGrade: number = 0;
  comment: string = '';
  ngOnInit() {
    this.renderer.appendChild(document.body, this.elementRef.nativeElement);
  }

  submit() {
    const payload = {
      raterId: this.raterId,
      vehicleGrade: this.vehicleGrade,
      driverGrade: this.driverGrade,
      comment: this.comment
    };

    const url = `http://localhost:8081/api/rides/${this.rideId}/ratings`;
    
    this.http.post(url, payload).subscribe({
      next: (response) => {
        console.log('Rating sent successfully', response);
        this.submitted.emit(response);
        this.close.emit();
      },
      error: (err) => console.error('Failed to send rating', err)
    });
  }
}