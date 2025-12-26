export type MessageId =
  | 'driver-profile-edit-submitted'
  | 'confirm-logout';

export type MessageAction =
  | {
      kind: 'navigate';
      text: string;
      url: string;
      variant?: 'primary' | 'ghost';
    }
  | {
      kind: 'logout';
      text: string;
      urlAfter: string;
      toast?: string;
      variant?: 'primary' | 'ghost';
    };

export type MessageConfig = {
  title: string;
  description: string;
  actions: MessageAction[];
};

export const MESSAGE_REGISTRY: Record<MessageId, MessageConfig> = {
  'driver-profile-edit-submitted': {
    title: 'Profile update submitted',
    description:
      'Your profile changes have been submitted and are waiting for administrator approval. They will become visible once approved.',
    actions: [
      { kind: 'navigate', text: 'Done', url: '/profile', variant: 'primary' },
    ],
  },

  'confirm-logout': {
    title: 'Log out',
    description: 'Are you sure you want to log out?',
    actions: [
      { kind: 'navigate', text: 'Cancel', url: '/profile', variant: 'ghost' },
      {
        kind: 'logout',
        text: 'Log out',
        urlAfter: '/',
        toast: 'Logged out successfully!',
        variant: 'primary',
      },
    ],
  },
};
