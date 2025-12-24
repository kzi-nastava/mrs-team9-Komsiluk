import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ActivationMessageComponent } from './activation-message.component';

describe('ActivationMessageComponent', () => {
  let component: ActivationMessageComponent;
  let fixture: ComponentFixture<ActivationMessageComponent>;
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ActivationMessageComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ActivationMessageComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
