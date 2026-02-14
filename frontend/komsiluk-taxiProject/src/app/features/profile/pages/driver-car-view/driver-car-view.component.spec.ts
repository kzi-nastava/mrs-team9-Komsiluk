import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { DriverCarViewComponent } from './driver-car-view.component';

describe('DriverCarViewComponent', () => {
  let component: DriverCarViewComponent;
  let fixture: ComponentFixture<DriverCarViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DriverCarViewComponent],
      providers: [provideRouter([])]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DriverCarViewComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
