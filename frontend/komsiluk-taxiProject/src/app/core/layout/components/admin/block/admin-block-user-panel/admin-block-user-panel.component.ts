import { Component, OnDestroy, signal} from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { debounceTime, distinctUntilChanged, switchMap, catchError, of, Subscription, tap } from 'rxjs';
import { ToastService } from '../../../../../../shared/components/toast/toast.service';
import { BlockUserConfirmModalService } from '../../../../../../shared/components/modal-shell/services/block-user-confirm-modal.service';
import { AdminUserService } from '../services/admin-user.service';
import { finalize } from 'rxjs/operators';
import { BlockNoteService } from '../services/block-note.service';
import { AuthService } from '../../../../../auth/services/auth.service';
import { BlockNoteCreateDTO } from '../../../../../../shared/models/block-note.model';
import { HttpErrorResponse } from '@angular/common/http';

type EmailSuggestion = { email: string; display: string };

@Component({
  selector: 'app-admin-block-user-panel',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './admin-block-user-panel.component.html',
  styleUrl: './admin-block-user-panel.component.css'
})
export class AdminBlockUserPanelComponent implements OnDestroy {
  emailCtrl = new FormControl<string>('', { nonNullable: true });

  suggestions = signal<EmailSuggestion[]>([]);
  loading = signal(false);
  blocking = signal(false);

  private sub?: Subscription;

  private pickedEmail: string | null = null;
  pickedFromAutocomplete = false;

  constructor(
    private api: AdminUserService,
    private toast: ToastService,
    private blockModal: BlockUserConfirmModalService,
    private blockNoteApi: BlockNoteService,
    private auth: AuthService
  ) {
    this.sub = this.emailCtrl.valueChanges.pipe(
      tap(v => {
        const txt = (v ?? '').trim();

        if (!this.pickedEmail || txt !== this.pickedEmail) {
          this.pickedFromAutocomplete = false;
        }
      }),
      debounceTime(250),
      distinctUntilChanged(),
      switchMap(v => {
        const q = (v ?? '').trim();

        if (this.pickedFromAutocomplete && this.pickedEmail && q === this.pickedEmail) {
          this.loading.set(false);
          this.suggestions.set([]);
          return of<string[]>([]);
        }

        if (q.length < 2) {
          this.loading.set(false);
          this.suggestions.set([]);
          return of<string[]>([]);
        }

        this.loading.set(true);
        return this.api.autocompleteEmails(q, 8).pipe(
          catchError(() => of<string[]>([])),
          finalize(() => this.loading.set(false))
        );
      })
    ).subscribe(list => {
      const current = (this.emailCtrl.value ?? '').trim();
      if (this.pickedFromAutocomplete && this.pickedEmail && current === this.pickedEmail) return;

      const cleaned = (list ?? [])
        .map(x => String(x).trim())
        .filter(Boolean)
        .map(email => ({ email, display: email } as EmailSuggestion));

      this.suggestions.set(cleaned);
    });
  }

  ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }

  pickEmail(em: string) {
    const email = (em ?? '').trim();
    if (!email) return;

    this.pickedEmail = email;
    this.pickedFromAutocomplete = true;

    this.emailCtrl.setValue(email, { emitEvent: false });

    this.loading.set(false);
    this.suggestions.set([]);
  }

  canBlock(): boolean {
    return this.pickedFromAutocomplete && !!this.pickedEmail;
  }

  onBlockClick() {
    if (!this.canBlock()) {
      this.toast.show('Please select an email from autocomplete.');
      return;
    }

    const email = this.pickedEmail!;
    const adminId = this.auth.userId();

    if (!adminId) {
      this.toast.show('Not logged in.');
      return;
    }

    this.blockModal.openModal({ email }, (reason) => {
      const dto: BlockNoteCreateDTO = {
        blockedUserEmail: email,
        adminId: Number(adminId),
        reason: String(reason ?? '').trim(),
      };

      if (!dto.reason) {
        this.toast.show('Please enter a reason.');
        return;
      }

      this.blocking.set(true);

      this.blockNoteApi.create(dto).pipe(
        finalize(() => this.blocking.set(false))
      ).subscribe({
        next: () => {
          this.toast.show('User blocked successfully.');

          this.pickedEmail = null;
          this.pickedFromAutocomplete = false;
          this.emailCtrl.setValue('', { emitEvent: false });
          this.suggestions.set([]);
        },
        error: (err: HttpErrorResponse) => {
          const msg =
            err?.error?.message ||
            err?.error ||
            err?.message ||
            'Failed to block user.';
          this.toast.show(String(msg));
        }
      });
    });
  }
}