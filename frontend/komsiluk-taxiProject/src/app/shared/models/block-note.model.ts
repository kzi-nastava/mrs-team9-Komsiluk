export interface BlockNoteCreateDTO {
  blockedUserEmail: string;
  adminId: number;
  reason: string;
}

export interface BlockNoteResponseDTO {
  id: number;
  blockedUserEmail: string;
  adminEmail: string;
  reason: string;
  createdAt: string;
}

export interface UserBlockedDTO {
    blocked: boolean;
}