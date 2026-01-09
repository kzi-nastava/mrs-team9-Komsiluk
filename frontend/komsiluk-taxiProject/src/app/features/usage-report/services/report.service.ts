import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RideReportDTO } from '../../../shared/models/report.model';

@Injectable({ providedIn: 'root' })
export class ReportService {
  private readonly base = 'http://localhost:8081/api/reports';

  constructor(private http: HttpClient) {}

  private params(start: string, end: string) {
    return new HttpParams().set('start', start).set('end', end);
  }

  getUserReport(userId: number, start: string, end: string): Observable<RideReportDTO> {
    return this.http.get<RideReportDTO>(`${this.base}/users/${userId}`, { params: this.params(start, end) });
  }

  getAllDriversReport(start: string, end: string): Observable<RideReportDTO> {
    return this.http.get<RideReportDTO>(`${this.base}/drivers`, { params: this.params(start, end) });
  }

  getAllPassengersReport(start: string, end: string): Observable<RideReportDTO> {
    return this.http.get<RideReportDTO>(`${this.base}/passengers`, { params: this.params(start, end) });
  }

  getUserReportByEmail(email: string, start: string, end: string): Observable<RideReportDTO> {
    const params = this.params(start, end).set('email', email);
    return this.http.get<RideReportDTO>(`${this.base}/users/by-email`, { params });
  }
}
