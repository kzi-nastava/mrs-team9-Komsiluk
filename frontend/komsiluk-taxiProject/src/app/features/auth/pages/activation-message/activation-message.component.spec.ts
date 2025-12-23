import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ActivationMessage } from './activation-message.component';

describe('ActivationMessage', () => {
  let component: ActivationMessage;
  let fixture: ComponentFixture<ActivationMessage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ActivationMessage]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ActivationMessage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
