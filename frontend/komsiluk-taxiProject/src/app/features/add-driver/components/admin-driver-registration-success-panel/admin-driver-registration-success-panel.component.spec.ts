import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';

import { AdminDriverRegistrationSuccessPanelComponent } from './admin-driver-registration-success-panel.component';

describe('AdminDriverRegistrationSuccessPanelComponent', () => {
  let component: AdminDriverRegistrationSuccessPanelComponent;
  let fixture: ComponentFixture<AdminDriverRegistrationSuccessPanelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminDriverRegistrationSuccessPanelComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(AdminDriverRegistrationSuccessPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render success message and emit done on button click', () => {
    const text = (fixture.nativeElement as HTMLElement).textContent || '';
    expect(text).toContain('Driver account created');
    expect(text).toContain('activation email');

    spyOn(component.done, 'emit');

    const btn = fixture.debugElement.query(By.css('button.btn--primary'));
    btn.triggerEventHandler('click', null);

    expect(component.done.emit).toHaveBeenCalled();
  });
});
