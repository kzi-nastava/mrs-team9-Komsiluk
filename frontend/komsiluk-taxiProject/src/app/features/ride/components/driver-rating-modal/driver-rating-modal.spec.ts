import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DriverRatingModalComponent } from './driver-raitng-modal';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ToastService } from '../../../../shared/components/toast/toast.service';

describe('DriverRatingModalComponent', () => {

  let component: DriverRatingModalComponent;
  let fixture: ComponentFixture<DriverRatingModalComponent>;
  let httpMock: HttpTestingController;
  let toastMock: jasmine.SpyObj<ToastService>;

  beforeEach(async () => {

    toastMock = jasmine.createSpyObj('ToastService', ['show']);

    await TestBed.configureTestingModule({
      imports: [
        DriverRatingModalComponent,
        HttpClientTestingModule
      ],
      providers: [
        { provide: ToastService, useValue: toastMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(DriverRatingModalComponent);
    component = fixture.componentInstance;

    component.rideId = 1;
    component.raterId = 2;

    httpMock = TestBed.inject(HttpTestingController);

    fixture.detectChanges();
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should append component to document body on init', () => {
    const appendSpy = spyOn(document.body, 'appendChild').and.callThrough();
    component.ngOnInit();
    expect(appendSpy).toHaveBeenCalled();
  });

  describe('initial state', () => {

    it('should create component', () => {
      expect(component).toBeTruthy();
    });

    it('submit button should be disabled when grades are not selected', () => {
      component.driverGrade = 0;
      component.vehicleGrade = 0;
      fixture.detectChanges();

      const button: HTMLButtonElement =
        fixture.nativeElement.querySelector('.btn-submit');

      expect(button.disabled).toBeTrue();
    });

    it('submit button should be enabled when both grades are selected', () => {
      component.driverGrade = 4;
      component.vehicleGrade = 5;
      fixture.detectChanges();

      const button: HTMLButtonElement =
        fixture.nativeElement.querySelector('.btn-submit');

      expect(button.disabled).toBeFalse();
    });

  });

  describe('submit success flow', () => {

    beforeEach(() => {
      component.driverGrade = 5;
      component.vehicleGrade = 4;
      component.comment = 'Excellent ride';
      fixture.detectChanges();
    });

    it('should call correct endpoint with correct payload', () => {

      component.submit();

      const req = httpMock.expectOne(
        'http://localhost:8081/api/rides/1/ratings'
      );

      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual({
        raterId: 2,
        vehicleGrade: 4,
        driverGrade: 5,
        comment: 'Excellent ride'
      });

      req.flush({});
    });

    it('should emit submitted and close and show success toast', () => {

      spyOn(component.submitted, 'emit');
      spyOn(component.close, 'emit');

      component.submit();

      const req = httpMock.expectOne(
        'http://localhost:8081/api/rides/1/ratings'
      );

      req.flush({ id: 123 });

      expect(component.submitted.emit).toHaveBeenCalledTimes(1);
      expect(component.close.emit).toHaveBeenCalledTimes(1);
      expect(toastMock.show)
        .toHaveBeenCalledWith('Rating submitted successfully.');
    });

  });

  describe('submit error flow', () => {

    beforeEach(() => {
      component.driverGrade = 3;
      component.vehicleGrade = 3;
      fixture.detectChanges();
    });

    it('should show backend message when provided', () => {

      spyOn(component.close, 'emit');

      component.submit();

      const req = httpMock.expectOne(
        'http://localhost:8081/api/rides/1/ratings'
      );

      req.flush(
        { message: 'Custom backend error' },
        { status: 400, statusText: 'Bad Request' }
      );

      expect(toastMock.show)
        .toHaveBeenCalledWith('Custom backend error');

      expect(component.close.emit).toHaveBeenCalledTimes(1);
    });

    it('should fallback to err.message when backend does not provide message', () => {

      spyOn(component.close, 'emit');

      component.submit();

      const req = httpMock.expectOne(
        'http://localhost:8081/api/rides/1/ratings'
      );

      req.error(new ErrorEvent('Network error'));

      expect(toastMock.show).toHaveBeenCalled();

      const calledArg = toastMock.show.calls.mostRecent().args[0];
      expect(calledArg).toContain('Http failure response');

      expect(component.close.emit).toHaveBeenCalledTimes(1);
    });

  });

  describe('UI interaction', () => {

    it('should update driverGrade when driver star is clicked', () => {

      const stars = fixture.nativeElement.querySelectorAll('.star');

      stars[4].click();
      fixture.detectChanges();

      expect(component.driverGrade).toBeGreaterThan(0);
    });

    it('should update vehicleGrade when vehicle star is clicked', () => {

      const stars = fixture.nativeElement.querySelectorAll('.star');

      stars[9].click();
      fixture.detectChanges();

      expect(component.vehicleGrade).toBeGreaterThan(0);
    });

  });

});
