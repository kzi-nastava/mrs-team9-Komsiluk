import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ForgotPasswordMessageComponent } from './forgot-password-message.component';

describe('ForgotPasswordMessageComponent', () => {
  let component: ForgotPasswordMessageComponent;
  let fixture: ComponentFixture<ForgotPasswordMessageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ForgotPasswordMessageComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ForgotPasswordMessageComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
