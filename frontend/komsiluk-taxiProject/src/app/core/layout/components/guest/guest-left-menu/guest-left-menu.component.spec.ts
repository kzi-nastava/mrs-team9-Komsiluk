import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GuestLeftMenuComponent } from './guest-left-menu.component';

describe('GuestLeftMenuComponent', () => {
  let component: GuestLeftMenuComponent;
  let fixture: ComponentFixture<GuestLeftMenuComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GuestLeftMenuComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GuestLeftMenuComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
