import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AdminUserService {
  private readonly API = 'http://localhost:8081/api/users';

  constructor(private http: HttpClient) {}

  autocompleteEmails(query: string, limit = 8): Observable<string[]> {
    const params = new HttpParams()
      .set('query', query ?? '')
      .set('limit', String(limit));

    return this.http.get<string[]>(`${this.API}/emails/autocomplete`, { params });
  }
}
