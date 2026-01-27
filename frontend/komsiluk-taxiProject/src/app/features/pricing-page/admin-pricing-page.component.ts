import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core'; 
import { CommonModule } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/auth/services/auth.service';

export interface PricingResponseDTO {
  vehicleType: string;
  startingPrice: number;
  pricePerKm: number;
}

@Component({
  selector: 'app-admin-pricing-page',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-pricing-page.component.html',
  styleUrls: ['./admin-pricing-page.component.css']
})
export class AdminPricingPageComponent implements OnInit {
  private http = inject(HttpClient);
  private authService = inject(AuthService);
  private cdr = inject(ChangeDetectorRef); 
  
  pricingList: PricingResponseDTO[] = [];
  isLoading = true;

  ngOnInit() {
    this.fetchPricing();
  }

  private getHeaders() {
    const token = this.authService.getToken();
    return new HttpHeaders().set('Authorization', `Bearer ${token}`);
  }

  fetchPricing() {
    this.isLoading = true;
    this.http.get<PricingResponseDTO[]>('http://localhost:8081/api/admin/pricing', {
      headers: this.getHeaders()
    })
    .subscribe({
      next: (data) => {
        this.pricingList = data;
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Fetch error:', err);
        this.isLoading = false;
      }
    });
  }

  saveChanges(item: PricingResponseDTO) {
    const body = {
      startingPrice: item.startingPrice,
      pricePerKm: item.pricePerKm
    };

    this.http.put(`http://localhost:8081/api/admin/pricing/${item.vehicleType}`, body, {
      headers: this.getHeaders()
    })
    .subscribe({
      next: () => alert(`Price for ${item.vehicleType} updated!`),
      error: (err) => alert('Save failed: ' + (err.error?.message || 'Check permissions'))
    });
  }
}