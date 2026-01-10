import { Component, signal, computed, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ToastService } from '../../../shared/components/toast/toast.service';
import { DriverChangeRequestReviewDialogComponent } from '../driver-change-request-review-dialog/driver-change-request-review-dialog.component';
import { DriverEditRequestResponseDTO, UserProfileResponseDTO } from '../../../shared/models/profile.models';
import { ProfileService } from '../../profile/services/profile.service';
import { forkJoin, of } from 'rxjs';
import { catchError, finalize, map, switchMap } from 'rxjs/operators';

type DiffRow = {
  key: string;
  label: string;
  current: string;
  requested: string;
};

@Component({
  selector: 'app-admin-driver-change-requests-page',
  standalone: true,
  imports: [CommonModule, DriverChangeRequestReviewDialogComponent],
  templateUrl: './admin-driver-change-requests-page.component.html',
  styleUrls: ['./admin-driver-change-requests-page.component.css'],
})
export class AdminDriverChangeRequestsPageComponent implements OnInit {
  loading = signal(false);

  requests = signal<DriverEditRequestResponseDTO[]>([]);
  currentByDriverId = signal<Record<number, UserProfileResponseDTO>>({});

  dialogOpen = signal(false);
  selectedReq = signal<DriverEditRequestResponseDTO | null>(null);
  selectedCurrent = signal<UserProfileResponseDTO | null>(null);

  constructor(
    private toast: ToastService,
    private profileService: ProfileService
  ) {}

  ngOnInit(): void {
    this.loadPending();
  }

  private loadPending() {
    this.loading.set(true);

    this.profileService.getPendingDriverEditRequests().pipe(
      switchMap((reqs) => {
        this.requests.set(reqs ?? []);

        const ids = Array.from(new Set((reqs ?? []).map(r => r.driverId).filter(Boolean)));

        if (ids.length === 0) return of({ ids: [] as number[], profiles: [] as (UserProfileResponseDTO | null)[] });

        return forkJoin(
          ids.map(id =>
            this.profileService.getProfileById(id).pipe(
              catchError(() => of(null))
            )
          )
        ).pipe(
          map((profiles) => ({ ids, profiles }))
        );
      }),
      finalize(() => this.loading.set(false))
    ).subscribe({
      next: ({ ids, profiles }) => {
        const nextMap: Record<number, UserProfileResponseDTO> = { ...this.currentByDriverId() };

        ids.forEach((id, i) => {
          const p = profiles[i];
          if (p) nextMap[id] = p;
        });

        this.currentByDriverId.set(nextMap);
      },
      error: (err) => {
        console.error(err);
        this.toast.show(err?.error?.message || 'Failed to load pending requests.');
      }
    });
  }

  // ===== helpers =====
  private fmtBool(v: boolean) { return v ? 'Yes' : 'No'; }
  private safeStr(v: any) { return (v === null || v === undefined || v === '') ? '—' : String(v); }

  private buildDiff(current: UserProfileResponseDTO, req: DriverEditRequestResponseDTO): DiffRow[] {
    const rows: DiffRow[] = [];

    const add = (key: string, label: string, cur: any, next: any, formatter?: (x: any) => string) => {
      if (next === null || next === undefined) return;
      const curS = formatter ? formatter(cur) : this.safeStr(cur);
      const nextS = formatter ? formatter(next) : this.safeStr(next);
      if (curS !== nextS) rows.push({ key, label, current: curS, requested: nextS });
    };

    add('name', 'First name', current.firstName, req.newName);
    add('surname', 'Last name', current.lastName, req.newSurname);
    add('address', 'Address', current.address, req.newAddress);
    add('city', 'City', current.city, req.newCity);
    add('phone', 'Phone number', current.phoneNumber, req.newPhoneNumber);

    add('model', 'Car model', current.vehicle?.model, req.newModel);
    add('type', 'Vehicle type', current.vehicle?.type, req.newType);
    add('plate', 'Licence plate', current.vehicle?.licencePlate, req.newLicencePlate);
    add('seats', 'Seat count', current.vehicle?.seatCount, req.newSeatCount);

    add('baby', 'Baby friendly', current.vehicle?.babyFriendly, req.newBabyFriendly, (x) => this.fmtBool(!!x));
    add('pet', 'Pet friendly', current.vehicle?.petFriendly, req.newPetFriendly, (x) => this.fmtBool(!!x));

    return rows;
  }

  getCurrentFor(req: DriverEditRequestResponseDTO): UserProfileResponseDTO {
    const map = this.currentByDriverId();
    return map[req.driverId] ?? {
      email: `driver${req.driverId}@neighborhood.taxi`,
      firstName: '—',
      lastName: '—',
      address: '—',
      city: '—',
      phoneNumber: '—',
      profileImageUrl: '',
      activeMinutesLast24h: 0,
      vehicle: null,
    };
  }

  requestChips(req: DriverEditRequestResponseDTO): string[] {
    const c = this.getCurrentFor(req);
    const diff = this.buildDiff(c, req);
    const keys = new Set(diff.map(d => d.key));

    const chips: string[] = [];
    const anyIdentity = ['name','surname','phone'].some(k => keys.has(k));
    const anyAddress  = ['address','city'].some(k => keys.has(k));
    const anyCar      = ['model','type','plate','seats'].some(k => keys.has(k));
    const anyPrefs    = ['baby','pet'].some(k => keys.has(k));

    if (anyIdentity) chips.push('Identity');
    if (anyAddress) chips.push('Address');
    if (anyCar) chips.push('Car');
    if (anyPrefs) chips.push('Preferences');
    if (!chips.length) chips.push('No changes');

    return chips;
  }

  openReview(req: DriverEditRequestResponseDTO) {
    const cur = this.getCurrentFor(req);
    this.selectedReq.set(req);
    this.selectedCurrent.set(cur);
    this.dialogOpen.set(true);
  }

  approveSelected() {
    const req = this.selectedReq();
    if (!req) return;

    this.loading.set(true);
    this.profileService.approveDriverEditRequest(req.id).pipe(
      finalize(() => this.loading.set(false))
    ).subscribe({
      next: () => {
        this.toast.show('Request approved.');
        this.requests.set(this.requests().filter(r => r.id !== req.id));
        this.dialogOpen.set(false);
      },
      error: (err) => {
        console.error(err);
        this.toast.show(err?.error?.message || 'Approve failed.');
      }
    });
  }

  rejectSelected() {
    const req = this.selectedReq();
    if (!req) return;

    this.loading.set(true);
    this.profileService.rejectDriverEditRequest(req.id).pipe(
      finalize(() => this.loading.set(false))
    ).subscribe({
      next: () => {
        this.toast.show('Request rejected.');
        this.requests.set(this.requests().filter(r => r.id !== req.id));
        this.dialogOpen.set(false);
      },
      error: (err) => {
        console.error(err);
        this.toast.show(err?.error?.message || 'Reject failed.');
      }
    });
  }

  closeDialog() {
    this.dialogOpen.set(false);
  }

  diffRows = computed<DiffRow[]>(() => {
    const req = this.selectedReq();
    const cur = this.selectedCurrent();
    if (!req || !cur) return [];
    return this.buildDiff(cur, req);
  });
}
