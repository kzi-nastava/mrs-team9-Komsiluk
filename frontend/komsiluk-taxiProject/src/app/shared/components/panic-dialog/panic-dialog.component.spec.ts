import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PanicDialogComponent } from './panic-dialog.component';

describe('PanicDialogComponent', () => {
  let component: PanicDialogComponent;
  let fixture: ComponentFixture<PanicDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PanicDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PanicDialogComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
