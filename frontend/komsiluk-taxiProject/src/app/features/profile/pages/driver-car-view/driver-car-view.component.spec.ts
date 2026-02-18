import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';

import { DriverCarViewComponent } from './driver-car-view.component';

describe('DriverCarViewComponent', () => {
  let component: DriverCarViewComponent;
  let fixture: ComponentFixture<DriverCarViewComponent>;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DriverCarViewComponent],
      providers: [
        provideRouter([]),
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    }).compileComponents();

    httpMock = TestBed.inject(HttpTestingController);

    fixture = TestBed.createComponent(DriverCarViewComponent);
    component = fixture.componentInstance;

    fixture.detectChanges();
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should create', () => {
    const req = httpMock.expectOne((r) =>
      r.method === 'GET' &&
      r.url === 'http://localhost:8081/api/rides/driver/null/current'
    );

    req.flush([]);

    expect(component).toBeTruthy();
  });
});
