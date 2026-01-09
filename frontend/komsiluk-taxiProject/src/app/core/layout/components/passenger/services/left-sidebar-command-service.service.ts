import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

export type LeftMenuSection = 'book' | 'fav' | 'sched';

export type LeftMenuCommand = {
  section: LeftMenuSection;
  scrollId?: string;
};

@Injectable({ providedIn: 'root' })
export class LeftSidebarCommandService {
  private _cmd$ = new Subject<LeftMenuCommand>();
  cmd$ = this._cmd$.asObservable();

  emit(cmd: LeftMenuCommand) {
    this._cmd$.next(cmd);
  }
}
