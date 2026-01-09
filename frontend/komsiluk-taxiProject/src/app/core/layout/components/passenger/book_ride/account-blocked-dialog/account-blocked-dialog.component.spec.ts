import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AccountBlockedDialogComponent } from './account-blocked-dialog.component';

describe('AccountBlockedDialogComponent', () => {
  let component: AccountBlockedDialogComponent;
  let fixture: ComponentFixture<AccountBlockedDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AccountBlockedDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AccountBlockedDialogComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
