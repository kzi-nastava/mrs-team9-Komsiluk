import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators, FormControl } from '@angular/forms';
import { AuthService, UserRole } from '../../../../core/auth/services/auth.service';
import { ToastService } from '../../../../shared/components/toast/toast.service';
import { AdminUserService } from '../../../../core/layout/components/admin/block/services/admin-user.service';
import { debounceTime, distinctUntilChanged, switchMap, catchError, of, tap, finalize , filter, map} from 'rxjs';
import { ReportService } from '../../services/report.service';
import { RideReportDTO } from '../../../../shared/models/report.model';
import { ChartConfiguration, ChartOptions } from 'chart.js';
import { BaseChartDirective } from 'ng2-charts';

type Target = 'ALL_DRIVERS' | 'ALL_PASSENGERS' | 'SINGLE_USER';
type EmailSuggestion = { email: string; display: string };

@Component({
  selector: 'app-usage-reports-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, BaseChartDirective],
  templateUrl: './usage-reports-page.component.html',
  styleUrls: ['./usage-reports-page.component.css']
})
export class UsageReportsPageComponent {
  loading = signal(false);
  form: any;
  private suppressNextEmailSearch = false;

  isAdmin(): boolean {
    return this.auth.userRole() === UserRole.ADMIN;
  }

  emailCtrl = new FormControl<string>('', { nonNullable: true });
  suggestions = signal<EmailSuggestion[]>([]);
  emailLoading = signal(false);

  private pickedEmail: string | null = null;
  pickedFromAutocomplete = false;
  private suppressNextAutocomplete = false;

  report=signal<RideReportDTO | null>(null);
  private accent = this.readAccentColor();

  ridesChartData: ChartConfiguration<'line'>['data'] = { labels: [], datasets: [] };
  kmChartData: ChartConfiguration<'line'>['data'] = { labels: [], datasets: [] };
  moneyChartData: ChartConfiguration<'line'>['data'] = { labels: [], datasets: [] };

  chartOptions: ChartOptions<'line'> = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { display: false },
      tooltip: { enabled: true }
    },
    scales: {
      x: {
        ticks: { maxRotation: 0, autoSkip: true },
        grid: { display: false }
      },
      y: {
        grid: { color: 'rgba(0,0,0,0.08)' }
      }
    },
    elements: {
      line: { tension: 0.25, borderWidth: 3 },
      point: { radius: 3, hoverRadius: 5 }
    }
  };

  constructor(
    private fb: FormBuilder,
    private auth: AuthService,
    private toast: ToastService,
    private emailApi: AdminUserService,
    private reportService: ReportService
  ) {
    this.form = this.fb.group({
      from: ['', Validators.required],
      to: ['', Validators.required],

      target: ['ALL_DRIVERS' as Target],
      userEmail: [''],
    });

    this.emailCtrl.valueChanges.pipe(
      map(v => (v ?? '').trim()),

      filter((txt) => {
        if (this.suppressNextEmailSearch) {
          this.suppressNextEmailSearch = false;
          return false;
        }
        return true;
      }),

      tap((txt) => {
        if (!txt) {
          this.pickedEmail = null;
          this.pickedFromAutocomplete = false;
          this.suggestions.set([]);
          return;
        }

        if (!this.pickedEmail || txt !== this.pickedEmail) {
          this.pickedFromAutocomplete = false;
        }
      }),
      debounceTime(250),
      distinctUntilChanged(),
      switchMap(q => {
        if (q.length < 2) {
          this.emailLoading.set(false);
          this.suggestions.set([]);
          return of([]);
        }

        this.emailLoading.set(true);
        return this.emailApi.autocompleteEmails(q, 8).pipe(
          catchError(() => of([])),
          finalize(() => this.emailLoading.set(false))
        );
      })
    ).subscribe(list => {
      const cleaned = (list ?? [])
        .map(x => String(x).trim())
        .filter(Boolean)
        .map(email => ({ email, display: email }));

      this.suggestions.set(cleaned);
    });

    this.form.get('target')!.valueChanges.subscribe((t: string) => {
      if (t !== 'SINGLE_USER') {
        this.clearSingleUser();
      } else {
        this.clearSingleUser();
      }
    });

    this.resetCharts();
  }

  private clearSingleUser() {
    this.pickedEmail = null;
    this.pickedFromAutocomplete = false;
    this.suggestions.set([]);
    this.form.patchValue({ userEmail: '' }, { emitEvent: false });
    this.emailCtrl.setValue('', { emitEvent: false });
  }

  pickEmail(em: string) {
    const email = (em ?? '').trim();
    if (!email) return;

    this.pickedEmail = email;
    this.pickedFromAutocomplete = true;

    this.suggestions.set([]);
    this.emailLoading.set(false);

    this.suppressNextEmailSearch = true;
    this.emailCtrl.setValue(email);
  }

  private isValidDateRange(): boolean {
    const from = this.form.value.from;
    const to = this.form.value.to;
    if (!from || !to) return false;

    const f = new Date(from);
    const t = new Date(to);
    if (isNaN(+f) || isNaN(+t)) return false;

    return f <= t;
  }

  private hasValidPickedEmail(): boolean {
    const txt = (this.emailCtrl.value ?? '').trim();
    return !!this.pickedEmail && this.pickedFromAutocomplete && txt === this.pickedEmail;
  }

  canGenerate(): boolean {
    if (!this.isValidDateRange()) return false;

    if (this.isAdmin() && this.form.value.target === 'SINGLE_USER') {
      return this.hasValidPickedEmail();
    }

    return true;
  }

  onGenerate() {
    if (!this.isValidDateRange()) {
      this.toast.show('Please select a valid date range.');
      return;
    }

    if (this.isAdmin() && this.form.value.target === 'SINGLE_USER') {
      if (!this.hasValidPickedEmail()) {
        this.toast.show('Please select a user email from autocomplete.');
        return;
      }
    }

    const start = String(this.form.value.from);
    const end = String(this.form.value.to);

    this.loading.set(true);

    const role = this.auth.userRole();
    const myId = Number(this.auth.userId());

    let req$;

    if (role !== UserRole.ADMIN) {
      req$ = this.reportService.getUserReport(myId, start, end);
    } else {
      const t = this.form.value.target as Target;

      if (t === 'ALL_DRIVERS') req$ = this.reportService.getAllDriversReport(start, end);
      else if (t === 'ALL_PASSENGERS') req$ = this.reportService.getAllPassengersReport(start, end);
      else req$ = this.reportService.getUserReportByEmail(this.pickedEmail!, start, end);
    }

    req$.pipe(
      catchError((err) => {
        const msg = err?.error?.message || err?.error || err?.message || 'Failed to load report.';
        this.toast.show(String(msg));
        return of(null);
      }),
      finalize(() => this.loading.set(false))
    ).subscribe(dto => {
      this.report.set(dto);
      if (!dto) {
        this.resetCharts();
        return;
      }
      this.buildCharts(dto, start, end);
    });
  }

  showUserEmail(): boolean {
    return this.isAdmin() && this.form.value.target === 'SINGLE_USER';
  }

  private resetCharts() {
    this.ridesChartData = { labels: [], datasets: [] };
    this.kmChartData = { labels: [], datasets: [] };
    this.moneyChartData = { labels: [], datasets: [] };
  }

  private buildCharts(dto: RideReportDTO, start: string, end: string) {
    const days = this.buildDays(start, end);
    const labels = days.map(d => this.formatLabel(d));

    const ridesMap = this.toMap(dto.ridesPerDay);
    const kmMap = this.toMap(dto.distancePerDay);
    const moneyMap = this.toMap(dto.moneyPerDay);

    const rides = days.map(d => ridesMap.get(d) ?? 0);
    const kms = days.map(d => kmMap.get(d) ?? 0);
    const money = days.map(d => moneyMap.get(d) ?? 0);

    this.ridesChartData = {
      labels,
      datasets: [{
        data: rides,
        borderColor: this.accent,
        backgroundColor: 'rgba(255, 238, 2, 0.20)',
        fill: true
      }]
    };

    this.kmChartData = {
      labels,
      datasets: [{
        data: kms,
        borderColor: this.accent,
        backgroundColor: 'rgba(255, 238, 2, 0.20)',
        fill: true
      }]
    };

    this.moneyChartData = {
      labels,
      datasets: [{
        data: money,
        borderColor: this.accent,
        backgroundColor: 'rgba(255, 238, 2, 0.20)',
        fill: true
      }]
    };
  }

  private toMap(list: any[] | null | undefined): Map<string, number> {
    const m = new Map<string, number>();
    for (const x of (list ?? [])) {
      const key = String(x.date);
      const val = Number(x.value ?? 0);
      m.set(key, isFinite(val) ? val : 0);
    }
    return m;
  }

  private buildDays(start: string, end: string): string[] {
    const out: string[] = [];
    const s = new Date(start + 'T00:00:00');
    const e = new Date(end + 'T00:00:00');

    for (let d = new Date(s); d <= e; d.setDate(d.getDate() + 1)) {
      out.push(d.toISOString().slice(0, 10));
    }
    return out;
  }

  private formatLabel(isoDate: string): string {
    const [y, m, d] = isoDate.split('-');
    return `${d}.${m}`;
  }

  moneySum(): string {
    const r = this.report();
    if (!r) return '0';
    return this.formatMoney(r.totalMoney);
  }

  moneyAvg(): string {
    const r = this.report();
    if (!r) return '0';
    return this.formatMoney(r.averageMoneyPerDay);
  }

  ridesSum(): string {
    return String(this.report()?.totalRides ?? 0);
  }

  ridesAvg(): string {
    const v = Number(this.report()?.averageRidesPerDay ?? 0);
    return this.formatNumber(v);
  }

  kmSum(): string {
    const v = Number(this.report()?.totalDistanceKm ?? 0);
    return this.formatNumber(v);
  }

  kmAvg(): string {
    const v = Number(this.report()?.averageDistanceKmPerDay ?? 0);
    return this.formatNumber(v);
  }

  private formatNumber(n: number): string {
    const v = isFinite(n) ? n : 0;
    return new Intl.NumberFormat('en-US', { maximumFractionDigits: 2 }).format(v);
  }

  private formatMoney(x: number | string): string {
    const n = typeof x === 'string' ? Number(x) : Number(x);
    const v = isFinite(n) ? n : 0;
    return new Intl.NumberFormat('en-US', { maximumFractionDigits: 2 }).format(v);
  }

  private readAccentColor(): string {
    try {
      const v = getComputedStyle(document.documentElement).getPropertyValue('--color-secondary').trim();
      return v || '#FFEE02';
    } catch {
      return '#FFEE02';
    }
  }
}