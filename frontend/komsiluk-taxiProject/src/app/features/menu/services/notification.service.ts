import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface NotificationResponseDTO {
  id: number;
  userId: number;
  type: string;
  title: string;
  message: string;
  metadata: string | null;
  read: boolean;
  createdAt: string;
}

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private readonly API = 'http://localhost:8081/api/notifications';

  constructor(private http: HttpClient) {}

  getUnread(userId: number): Observable<NotificationResponseDTO[]> {
    return this.http.get<NotificationResponseDTO[]>(`${this.API}/user/${userId}/unread`);
  }

  markRead(id: number, read = true): Observable<NotificationResponseDTO> {
    return this.http.post<NotificationResponseDTO>(`${this.API}/${id}/read?read=${read}`, {});
  }
}
