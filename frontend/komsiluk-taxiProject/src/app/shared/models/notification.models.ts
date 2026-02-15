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
