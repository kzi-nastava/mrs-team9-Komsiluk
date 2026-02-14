import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DriverLeftMenuComponent } from './driver-left-menu.component';

describe('DriverLeftMenuComponent', () => {
  let component: DriverLeftMenuComponent;
  let fixture: ComponentFixture<DriverLeftMenuComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DriverLeftMenuComponent],
    })
    .compileComponents();

    fixture = TestBed.createComponent(DriverLeftMenuComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});