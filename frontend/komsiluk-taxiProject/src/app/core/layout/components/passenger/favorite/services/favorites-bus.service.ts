import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class FavoritesBusService {
  private refreshSubject = new Subject<void>();
  refresh$ = this.refreshSubject.asObservable();

  trigger() {
    this.refreshSubject.next();
  }
}
