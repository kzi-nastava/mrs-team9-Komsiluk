export interface ChatMessageDTO {
  id?: number;
  senderId: number;
  senderEmail: string;
  receiverId: number;
  content: string;
  sentAt: string;
  type: 'FROM_USER' | 'FROM_ADMIN';
}