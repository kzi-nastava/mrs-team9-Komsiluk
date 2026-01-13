import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoginRequiredDialogComponent } from './login-required-dialog.component';

describe('LoginRequiredDialogComponent', () => {
  let component: LoginRequiredDialogComponent;
  let fixture: ComponentFixture<LoginRequiredDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoginRequiredDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LoginRequiredDialogComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
