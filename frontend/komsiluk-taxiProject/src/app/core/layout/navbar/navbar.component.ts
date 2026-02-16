import { Component, EventEmitter, Output } from '@angular/core';
import { Router, NavigationEnd, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService, UserRole } from '../../../core/auth/services/auth.service';
import { DriverRuntimeStateService } from '../components/driver/services/driver-runtime-state.service';
import { OnInit, OnDestroy, signal, Signal } from '@angular/core';
import { Subscription, filter } from 'rxjs';
import { NotificationService} from '../../../features/menu/services/notification.service';
import { NotificationSocketService } from '../../../core/services/notification-socket.service';
import { ToastService } from '../../../shared/components/toast/toast.service';
import { NotificationResponseDTO } from '../../../shared/models/notification.models';
import { ChatService, ChatMessageDTO } from '../../../shared/components/modal-shell/services/chat.service';
@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css'],
})
export class NavbarComponent implements OnInit, OnDestroy {
  @Output() menuClick = new EventEmitter<void>();
  @Output() rightMenuClick = new EventEmitter<void>();
  @Output() filterClick = new EventEmitter<void>();
  @Output() driverHistoryChange = new EventEmitter<boolean>();

  isHome = false;
  isDriverHistory = false;
  userRole: Signal<UserRole>;
  UserRole = UserRole;

  notifOpen = signal(false);
  notifications = signal<NotificationResponseDTO[]>([]);
  loading = signal(false);
  unreadCount = signal(0);

  private wsSub?: Subscription;
  private chatSub?: Subscription;
  private docClickHandler = (e: MouseEvent) => {
    if (this.notifOpen()) this.notifOpen.set(false);
  };

  constructor(private router: Router, private authService: AuthService, public driverState: DriverRuntimeStateService, private notificationService: NotificationService, private notificationSocket: NotificationSocketService, private toast: ToastService,private chatService: ChatService) {
    this.router.events.pipe(filter(e => e instanceof NavigationEnd)).subscribe(() => {
      this.isDriverHistory = this.router.url.startsWith('/driver-history');
      this.driverHistoryChange.emit(this.isDriverHistory);
    });
    this.router.events
      .pipe(filter(e => e instanceof NavigationEnd))
      .subscribe(() => {
        const path = this.router.url.split('?')[0].split('#')[0];
        this.isHome = path === '/' || path === '';
      });
    this.userRole = this.authService.userRole;
  }

  goHome() {
    this.router.navigateByUrl('/');
  }

  @Output() adminMenuClick = new EventEmitter<void>();

  ngOnInit(): void {
    document.addEventListener('click', this.docClickHandler);

    const token = this.getToken();
    const userId = this.getUserId();

    if (token) {
      this.notificationSocket.connect(token);

      this.wsSub = this.notificationSocket.lastNotification$.subscribe((n) => {
        if (!n) return;

        this.unreadCount.set(this.unreadCount() + 1);

        if (this.notifOpen()) {
          this.notifications.set([n, ...this.notifications()]);
        }

        this.toast.show(`${n.title}: ${n.message}`);
      });
      this.chatService.connect();

      this.chatSub = this.chatService.messages$.subscribe((msgs) => {
          if (msgs.length > 0) {
              const lastMsg = msgs[msgs.length - 1];
              
              const msgTime = new Date(lastMsg.sentAt).getTime();
              const now = new Date().getTime();
              
              if ((now - msgTime < 5000) && lastMsg.senderId !== userId) {
                  this.toast.show(`New message: ${lastMsg.content}`);
              }
          }
      });
    }

    if (userId != null) {
      this.refreshUnread(userId, true);
    }
  }

  ngOnDestroy(): void {
    document.removeEventListener('click', this.docClickHandler);
    this.wsSub?.unsubscribe();
    this.chatSub?.unsubscribe();
  }

  toggleNotifications(e: MouseEvent) {
    e.stopPropagation();

    const open = !this.notifOpen();
    this.notifOpen.set(open);

    if (open) {
      const userId = this.getUserId();
      if (userId != null) {
        this.refreshUnread(userId, false);
      }
    }
  }

  private refreshUnread(userId: number, onlyCount: boolean) {
    this.loading.set(!onlyCount);

    this.notificationService.getUnread(userId).subscribe({
      next: (list) => {
        this.unreadCount.set(list.length);
        if (!onlyCount) this.notifications.set(list);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
      }
    });
  }

  openNotification(n: NotificationResponseDTO) {
    this.notificationService.markRead(n.id, true).subscribe({
      next: () => {
        this.notifications.set(this.notifications().filter(x => x.id !== n.id));
        this.unreadCount.set(Math.max(0, this.unreadCount() - 1));
      }
    });
  }

  markAllAsRead() {
    const list = this.notifications();
    if (list.length === 0) return;

    this.notifications.set([]);
    this.unreadCount.set(0);

    for (const n of list) {
      this.notificationService.markRead(n.id, true).subscribe();
    }
  }

  formatTime(iso: string): string {
    const d = new Date(iso);
    return d.toLocaleString('sr-RS', {
      day: '2-digit', month: '2-digit', year: 'numeric',
      hour: '2-digit', minute: '2-digit'
    });
  }

  private getToken(): string | null {
    return localStorage.getItem('auth_token');
  }

  private getUserId(): number | null {
    return this.authService.userId() ?? null;
  }

  isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }
}
