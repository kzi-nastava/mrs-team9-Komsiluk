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
      this.loadInbox();

      this.msgSub = this.chatService.messages$.subscribe(msgs => {
        if (this.selectedContact) {
            const relevantMsgs = msgs.filter(m => 
                (m.senderId === this.selectedContact!.userId) || 
                (m.receiverId === this.selectedContact!.userId)
            );
            
            if (relevantMsgs.length !== this.messages.length || relevantMsgs.length > 0) {
                
                const lastMsg = relevantMsgs[relevantMsgs.length - 1];
                if (lastMsg && lastMsg.senderId === this.selectedContact.userId) {
                    this.chatService.markAsRead(this.selectedContact.userId).subscribe();
                }

                this.messages = relevantMsgs;
                this.cdr.detectChanges(); 
                this.scrollToBottom();
            }
        }

        const latestMsg = msgs[msgs.length - 1];
        if (latestMsg) {
            this.updateInboxList(latestMsg);
            this.cdr.detectChanges(); 
        }
      });

    } else {
      this.chatService.loadHistory(this.myId);
      
      this.msgSub = this.chatService.messages$.subscribe(msgs => {
        this.messages = msgs;
        this.cdr.detectChanges(); 
        this.scrollToBottom();
      });
    }
  }

  getProfileImage(contact: ChatInboxDTO): string {
      if (!contact.profilePicture) return '';

      if (contact.profilePicture.startsWith('data:')) {
          return contact.profilePicture;
      }

      if (contact.profilePicture.startsWith('/')) {
          return `${this.SERVER_URL}${contact.profilePicture}`;
      }

      return `data:image/jpeg;base64,${contact.profilePicture}`;
  }

  handleImageError(contact: ChatInboxDTO) {
      contact.profilePicture = undefined; 
  }


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

           if (this.userRole === UserRole.ADMIN && msg.senderId !== this.myId) {
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
    
    contact.unreadCount = 0;
    
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
      receiverId = 7; 
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