import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RecoveryActivation } from './recovery-activation';

describe('RecoveryActivation', () => {
  let component: RecoveryActivation;
  let fixture: ComponentFixture<RecoveryActivation>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RecoveryActivation]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RecoveryActivation);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
