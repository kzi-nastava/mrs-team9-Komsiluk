export type CardId =
  | 'login'

export const AUTH_CARD_REGISTRY: Record<
  CardId,
  { title: string; doneUrl: string; buttonText: string }
> = {
  'login': {
    title: 'Log In',
    doneUrl: '/',
    buttonText: 'Log in',
  },
};
