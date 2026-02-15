import { Injectable } from '@angular/core';
import { Client, IMessage } from '@stomp/stompjs';
import { BehaviorSubject } from 'rxjs';
import { NotificationResponseDTO } from '../../shared/models/notification.models';
import { NgZone } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class NotificationSocketService {
  private client: Client | null = null;
  private connected = false;

  public readonly lastNotification$ = new BehaviorSubject<NotificationResponseDTO | null>(null);

  constructor(private zone: NgZone) {}

  async connect(token: string) {
    if (this.connected) return;
    if (!token) return;

    const mod: any = await import('sockjs-client');
    const wsUrl = `http://localhost:8081/ws?token=${encodeURIComponent(token)}`;

    this.client = new Client({
      webSocketFactory: () => new mod.default(wsUrl),
      reconnectDelay: 3000,
      debug: (msg) => console.log('[STOMP]', msg),
    });

    this.client.onConnect = () => {
      this.connected = true;

      this.client?.subscribe('/user/queue/notifications', (message: IMessage) => {
        try {
          const payload = JSON.parse(message.body) as NotificationResponseDTO;
          this.zone.run(() => {
            console.log('[WS] notification received', payload);
            this.lastNotification$.next(payload);
          });
        } catch (e) {
        }
      });
    };

    this.client.onStompError = (frame) => {
    };

    this.client.onWebSocketClose = () => {
      this.connected = false;
    };

    this.client.activate();
  }

  disconnect() {
    if (!this.client) return;
    this.client.deactivate();
    this.client = null;
    this.connected = false;
  }

  isConnected() {
    return this.connected;
  }
}
