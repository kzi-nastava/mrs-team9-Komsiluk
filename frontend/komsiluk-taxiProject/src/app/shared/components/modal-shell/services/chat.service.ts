import { Injectable, NgZone } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Client, IMessage } from '@stomp/stompjs';
import { BehaviorSubject, Observable } from 'rxjs';

export interface ChatMessageDTO {
  id?: number;
  senderId: number;
  senderEmail: string;
  receiverId: number;
  content: string;
  sentAt: string;
  type: 'FROM_USER' | 'FROM_ADMIN';
}

export interface ChatInboxDTO {
  userId: number;
  email: string;
  fullName: string;
  lastMessage: string;
  lastMessageTime: string;
  profilePicture?: string;
  unreadCount: number;
}

@Injectable({ providedIn: 'root' })
export class ChatService {
  private client: Client | null = null;
  private connected = false;
  private connecting = false;
  private readonly API_URL = 'http://localhost:8081/api/chat';

  public readonly messages$ = new BehaviorSubject<ChatMessageDTO[]>([]);

  constructor(
    private http: HttpClient, 
    private zone: NgZone
  ) {}

  loadHistory(userId: number): void {
    this.http.get<ChatMessageDTO[]>(`${this.API_URL}/history/${userId}`)
      .subscribe({
        next: (history) => {
          this.messages$.next(history);
        },
        error: (err) => console.error('Failed to load history', err)
      });
  }

  getInbox(): Observable<ChatInboxDTO[]> {
    return this.http.get<ChatInboxDTO[]>(`${this.API_URL}/inbox`);
  }

  async connect() {
    // --- FIX: Ako smo povezani ILI se trenutno povezujemo, prekini ---
    if (this.connected || this.connecting) return;
    
    this.connecting = true; // Označi da je povezivanje u toku

    const token = localStorage.getItem('auth_token'); 
    if (!token) {
        this.connecting = false;
        return;
    }

    const mod: any = await import('sockjs-client');
    const wsUrl = `http://localhost:8081/ws?token=${encodeURIComponent(token)}`;

    this.client = new Client({
      webSocketFactory: () => new mod.default(wsUrl),
      reconnectDelay: 3000,
      debug: (msg) => console.log('[CHAT-STOMP]', msg),
    });

    this.client.onConnect = () => {
      this.connected = true;
      this.connecting = false; // Završeno povezivanje
      console.log('Chat Connected!');

      this.client?.subscribe('/user/queue/messages', (message: IMessage) => {
        try {
          const newMsg: ChatMessageDTO = JSON.parse(message.body);
          this.zone.run(() => {
            this.addMessageToState(newMsg);
          });
        } catch (e) {
          console.error('Error parsing message', e);
        }
      });
    };

    this.client.onStompError = (frame) => {
      console.error('Broker reported error: ' + frame.headers['message']);
      this.connecting = false; // Resetuj u slučaju greške
    };

    this.client.onWebSocketClose = () => {
      this.connected = false;
      this.connecting = false;
      console.log('Chat WebSocket closed');
    };

    this.client.activate();
  }

sendMessage(content: string, senderId: number, receiverId: number): void {
    if (this.client && this.connected) {
      const chatMessage = {
        senderId: senderId,
        receiverId: receiverId,
        content: content
      };

      this.client.publish({
        // PROMENA: Izbaci "chat" ako u Javi stoji samo /chat/send bez RequestMapping-a
        // Ali prema tvom kodu u Javi, @MessageMapping("/chat/send") zahteva:
        destination: '/app/chat/send', 
        body: JSON.stringify(chatMessage)
      });
    }
  }

  disconnect() {
    if (!this.client) return;
    this.client.deactivate();
    this.client = null;
    this.connected = false;
    this.connecting = false;
  }

  markAsRead(userId: number): Observable<void> {
    return this.http.put<void>(`${this.API_URL}/read/${userId}`, {});
  }

  private addMessageToState(msg: ChatMessageDTO) {
    const current = this.messages$.value;
    // Opciono: Provera duplikata za svaki slučaj (po ID-ju ako ga imaš, ili po vremenu)
    // Ali sa fix-om iznad ovo verovatno neće trebati.
    this.messages$.next([...current, msg]);
  }
}