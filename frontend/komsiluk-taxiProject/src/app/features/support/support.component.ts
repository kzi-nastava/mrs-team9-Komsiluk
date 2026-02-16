import { Component, OnInit, OnDestroy, ViewChild, ElementRef, AfterViewChecked, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService, UserRole } from '../../core/auth/services/auth.service';
import { ChatMessageDTO } from '../../shared/models/chat-message.model';
import { Subscription } from 'rxjs';
import { ChatInboxDTO, ChatService } from '../../shared/components/modal-shell/services/chat.service';

@Component({
  selector: 'app-support',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './support.html',
  styleUrls: ['./support.css']
})
export class SupportComponent implements OnInit, OnDestroy, AfterViewChecked {
  @ViewChild('scrollContainer') private scrollContainer!: ElementRef;

  // --- 1. URL SERVERA ZA SLIKE ---
  readonly SERVER_URL = 'http://localhost:8081'; 

  userRole: UserRole | null = null;
  UserRole = UserRole;
  myId: number | null = null;

  messages: ChatMessageDTO[] = [];
  newMessage: string = '';
  
  contacts: ChatInboxDTO[] = []; 
  selectedContact: ChatInboxDTO | null = null; 

  private msgSub!: Subscription;

  constructor(
    private chatService: ChatService, 
    private authService: AuthService,
    private cdr: ChangeDetectorRef 
  ) {}

  ngOnInit() {
    this.userRole = this.authService.userRole();
    this.myId = this.authService.userId();

    if (!this.myId) return;

    if (this.userRole === UserRole.ADMIN) {
      // --- ADMIN LOGIKA ---
      this.loadInbox();

      this.msgSub = this.chatService.messages$.subscribe(msgs => {
        // AŽURIRAJ CHAT PROZOR
        if (this.selectedContact) {
            const relevantMsgs = msgs.filter(m => 
                (m.senderId === this.selectedContact!.userId) || 
                (m.receiverId === this.selectedContact!.userId)
            );
            
            // Ako ima novih poruka ili promena
            if (relevantMsgs.length !== this.messages.length || relevantMsgs.length > 0) {
                
                // --- 2. READ STATUS LOGIKA (Live) ---
                // Ako stigne nova poruka OD korisnika koga trenutno gledamo,
                // odmah je označi kao pročitanu u bazi.
                const lastMsg = relevantMsgs[relevantMsgs.length - 1];
                if (lastMsg && lastMsg.senderId === this.selectedContact.userId) {
                    this.chatService.markAsRead(this.selectedContact.userId).subscribe();
                }

                this.messages = relevantMsgs;
                this.cdr.detectChanges(); 
                this.scrollToBottom();
            }
        }

        // AŽURIRAJ LISTU KONTAKATA
        const latestMsg = msgs[msgs.length - 1];
        if (latestMsg) {
            this.updateInboxList(latestMsg);
            this.cdr.detectChanges(); 
        }
      });

    } else {
      // --- USER LOGIKA ---
      this.chatService.loadHistory(this.myId);
      
      this.msgSub = this.chatService.messages$.subscribe(msgs => {
        this.messages = msgs;
        this.cdr.detectChanges(); 
        this.scrollToBottom();
      });
    }
  }

  // --- 3. IMAGE HANDLING METODE (Rešavaju problem sa slikama) ---

  getProfileImage(contact: ChatInboxDTO): string {
      if (!contact.profilePicture) return '';

      // A) Ako je Base64 sa prefixom
      if (contact.profilePicture.startsWith('data:')) {
          return contact.profilePicture;
      }

      // B) Ako je putanja sa servera (npr. /images/...)
      if (contact.profilePicture.startsWith('/')) {
          return `${this.SERVER_URL}${contact.profilePicture}`;
      }

      // C) Ako je čist Base64 string bez prefixa (najčešći slučaj kod tebe)
      return `data:image/jpeg;base64,${contact.profilePicture}`;
  }

  handleImageError(contact: ChatInboxDTO) {
      // Ako slika pukne, setujemo na undefined da bi HTML prikazao <i> ikonicu
      contact.profilePicture = undefined; 
  }

  // -------------------------------------------------------------

  loadInbox() {
    this.chatService.getInbox().subscribe({
      next: (data) => {
        this.contacts = data;
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Failed to load inbox', err)
    });
  }

  updateInboxList(msg: ChatMessageDTO) {
      const otherId = (msg.senderId === this.myId) ? msg.receiverId : msg.senderId;
      const contactIndex = this.contacts.findIndex(c => c.userId === otherId);

      if (contactIndex !== -1) {
           const contact = this.contacts[contactIndex];
           
           contact.lastMessage = msg.content;
           contact.lastMessageTime = msg.sentAt;

           // LOGIKA ZA BROJAČ (Samo Admin)
           if (this.userRole === UserRole.ADMIN && msg.senderId !== this.myId) {
               // Povećaj broj SAMO ako taj chat NIJE trenutno otvoren
               if (this.selectedContact?.userId !== otherId) {
                   contact.unreadCount = (contact.unreadCount || 0) + 1;
               }
           }

           this.contacts.splice(contactIndex, 1);
           this.contacts.unshift(contact);
           
           this.cdr.detectChanges(); 
      } else {
           if (this.userRole === UserRole.ADMIN) {
               this.loadInbox();
           }
      }
  }

  ngOnDestroy() {
    if (this.msgSub) this.msgSub.unsubscribe();
  }

  ngAfterViewChecked() {
    this.scrollToBottom();
  }

  selectContact(contact: ChatInboxDTO) {
    this.selectedContact = contact;
    
    // Vizuelno resetuj broj
    contact.unreadCount = 0;
    
    // --- 4. READ STATUS (Na klik) ---
    // Javi backendu da je sve pročitano
    this.chatService.markAsRead(contact.userId).subscribe({
        error: (err) => console.error('Error marking read', err)
    });

    this.chatService.loadHistory(contact.userId); 
  }

  sendMessage() {
    if (!this.newMessage.trim() || !this.myId) return;

    let receiverId: number;

    if (this.userRole === UserRole.ADMIN) {
      if (!this.selectedContact) return;
      receiverId = this.selectedContact.userId;
    } else {
      receiverId = 7; // ID Admina
    }

    this.chatService.sendMessage(this.newMessage, this.myId, receiverId);
    this.newMessage = '';
  }

  isMyMessage(msg: ChatMessageDTO): boolean {
    return msg.senderId === this.myId;
  }

  private scrollToBottom(): void {
    try {
      if (this.scrollContainer) {
        this.scrollContainer.nativeElement.scrollTop = this.scrollContainer.nativeElement.scrollHeight;
      }
    } catch(err) { }
  }
}