import { Component, signal } from '@angular/core';
import { ProfileSidebarComponent } from '../../components/profile-sidebar/profile-sidebar.component';
import { ProfileDetailsComponent } from '../../components/profile-details/profile-details.component';
import { ProfileService } from '../../services/profile.service';
import { UserProfileResponseDTO } from '../../../../shared/models/profile.models';
import { AuthService, UserRole } from '../../../../core/auth/services/auth.service';
import { ChangeDetectorRef } from '@angular/core';
import { forkJoin, of } from 'rxjs';
import { catchError, finalize, switchMap, tap } from 'rxjs/operators';
import { BlockNoteService } from '../../../../core/layout/components/admin/block/services/block-note.service';
import { BlockNoteResponseDTO, UserBlockedDTO } from '../../../../shared/models/block-note.model';
import { DriverBlockedDialogComponent } from '../../components/driver-blocked-dialog/driver-blocked-dialog.component';
import {ToastService} from '../../../../shared/components/toast/toast.service';

@Component({
  selector: 'app-profile-view',
  imports: [ProfileSidebarComponent, ProfileDetailsComponent, DriverBlockedDialogComponent],
  templateUrl: './profile-view.component.html',
  styleUrl: './profile-view.component.css',
})
export class ProfileViewComponent {
  profile: UserProfileResponseDTO | null = null;
  loading = false;

  avatarVersion = 0;

  isBlocked = signal(false);
  blockNote: BlockNoteResponseDTO | null = null;
  blockedDialogOpen = signal(false);

  constructor(private profileService: ProfileService, private auth: AuthService, private cdr: ChangeDetectorRef, private blockNoteService: BlockNoteService, private toast: ToastService) {}


  onProfileImagePicked(file: File) {
    if (!file.type.startsWith('image/')) {
      this.toast.show('Please pick an image file.');
      return;
    }
    if (file.size > 8 * 1024 * 1024) {
      this.toast.show('Image is too large (max 8MB).');
      return;
    }

    this.loading = true;

    this.profileService.updateMyProfileImage(file).pipe(
      finalize(() => {
        this.loading = false;
        this.cdr.detectChanges();
      })
    ).subscribe({
      next: (updated) => {
        this.profile = updated;

        this.avatarVersion++;

        this.toast.show('Profile image updated.');
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.toast.show(err?.error?.message || 'Upload failed.');
        this.cdr.detectChanges();
      }
    });
  }

  get isDriver(): boolean {
    return this.auth.userRole() === UserRole.DRIVER;
  }

  get isPassenger(): boolean {
    return this.auth.userRole() === UserRole.PASSENGER;
  }

  get activeToday(): string {
    if (!this.profile) {
      return '-';
    }
    const minutes = this.profile.activeMinutesLast24h;
    const hours = Math.floor(minutes / 60);
    const mins = minutes % 60;
    return `${hours}h ${mins}m`;
  }

  ngOnInit(): void {
    this.loading = true;

    const id = Number(this.auth.userId());

    if (!id) {
      this.profileService.getMyProfile().pipe(
        finalize(() => {
          this.loading = false;
          this.cdr.detectChanges();
        })
      ).subscribe({
        next: p => { this.profile = p; },
        error: () => {}
      });
      return;
    }

    forkJoin({
      profile: this.profileService.getMyProfile().pipe(
        catchError(() => of(null as any))
      ),
      blocked: this.profileService.isUserBlocked(id).pipe(
        catchError(() => of({ blocked: false } as UserBlockedDTO))
      )
    }).pipe(
      tap(({ profile, blocked }) => {
        this.profile = profile;
        this.isBlocked.set(!!blocked?.blocked);
      }),
      switchMap(({ blocked }) => {
        if (!blocked?.blocked) return of(null);
        return this.blockNoteService.getLastForUser(id).pipe(
          catchError(() => of(null))
        );
      }),
      finalize(() => {
        this.loading = false;
        this.cdr.detectChanges();
      })
    ).subscribe(note => {
      this.blockNote = note;
      this.cdr.detectChanges();
    });
  }

  openBlockedInfo() {
    this.blockedDialogOpen.set(true);
  }
}
