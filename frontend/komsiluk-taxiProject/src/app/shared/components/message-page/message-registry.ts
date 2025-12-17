export type MessageId =
  | 'driver-profile-edit-submitted'

export const MESSAGE_REGISTRY: Record<
  MessageId,
  { title: string; description: string; doneUrl: string; buttonText?: string }
> = {
  'driver-profile-edit-submitted': {
    title: 'Profile update submitted',
    description:
      'Your profile changes have been submitted and are waiting for administrator approval. They will become visible once approved.',
    doneUrl: '/profile',
    buttonText: 'Done',
  },
};
