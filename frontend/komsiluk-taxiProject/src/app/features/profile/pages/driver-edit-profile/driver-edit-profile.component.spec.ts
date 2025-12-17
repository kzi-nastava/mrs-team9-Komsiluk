import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DriverEditProfileComponent } from './driver-edit-profile.component';

describe('DriverEditProfileComponent', () => {
  let component: DriverEditProfileComponent;
  let fixture: ComponentFixture<DriverEditProfileComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DriverEditProfileComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DriverEditProfileComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
