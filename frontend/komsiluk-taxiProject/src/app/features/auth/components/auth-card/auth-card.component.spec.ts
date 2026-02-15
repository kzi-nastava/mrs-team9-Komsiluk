import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { AuthCardComponent } from './auth-card.component';

@Component({
  standalone: true,
  imports: [AuthCardComponent],
  template: `
    <app-auth-card title="Test Naslov">
      <div id="test-body">Glavni sadržaj</div>
      <button card-actions id="test-action">Dugme</button>
    </app-auth-card>
  `
})
class TestHostComponent {}

describe('AuthCardComponent', () => {
  let fixture: ComponentFixture<TestHostComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TestHostComponent, AuthCardComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(TestHostComponent);
    fixture.detectChanges();
  });

  it('should display the correct title from @Input', () => {
    const titleElement = fixture.debugElement.query(By.css('.card-title div'));
    expect(titleElement.nativeElement.textContent).toContain('Test Naslov');
  });

  it('should project body content into the correct slot', () => {
    const bodySlot = fixture.debugElement.query(By.css('.card-body'));
    const projectedBody = bodySlot.query(By.css('#test-body'));
    
    expect(projectedBody).toBeTruthy();
    expect(projectedBody.nativeElement.textContent).toBe('Glavni sadržaj');
  });

  it('should project actions into the card-actions slot using selector', () => {
    const actionSlot = fixture.debugElement.query(By.css('.card-actions'));
    const projectedAction = actionSlot.query(By.css('#test-action'));

    expect(projectedAction).toBeTruthy();
    expect(projectedAction.nativeElement.textContent).toBe('Dugme');
  });
});