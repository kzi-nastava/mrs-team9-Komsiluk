import { TestBed } from '@angular/core/testing';
import { RatingService, RatingCreateDTO, RatingResponseDTO } from './rating.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

describe('RatingService', () => {

  let service: RatingService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    });

    service = TestBed.inject(RatingService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });


  it('should send POST request to create rating', () => {

    const rideId = 5;

    const payload: RatingCreateDTO = {
      driverGrade: 5,
      vehicleGrade: 4,
      comment: 'Great ride'
    };

    const mockResponse: RatingResponseDTO = {
      id: 1,
      rideId: 5,
      raterId: 2,
      raterMail: 'test@mail.com',
      driverId: 10,
      vehicleId: 20,
      vehicleGrade: 4,
      driverGrade: 5,
      comment: 'Great ride',
      createdAt: '2026-02-14T00:00:00'
    };

    service.createRating(rideId, payload).subscribe(response => {
      expect(response).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`/api/rides/${rideId}/ratings`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(payload);

    req.flush(mockResponse);
  });

  it('should fetch ratings for ride', () => {

    const rideId = 7;

    const mockResponse: RatingResponseDTO[] = [];

    service.getRatingsForRide(rideId).subscribe(response => {
      expect(response).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(
      `http://localhost:8081/api/rides/${rideId}/ratings`
    );

    expect(req.request.method).toBe('GET');

    req.flush(mockResponse);
  });

  it('should fetch rating by rater id', () => {

    const rideId = 3;
    const raterId = 9;

    const mockResponse: RatingResponseDTO = {
      id: 2,
      rideId: 3,
      raterId: 9,
      raterMail: 'rater@mail.com',
      driverId: 1,
      vehicleId: 1,
      vehicleGrade: 5,
      driverGrade: 5,
      comment: 'Perfect',
      createdAt: '2026-02-14T00:00:00'
    };

    service.getRatingForRideByRater(rideId, raterId).subscribe(response => {
      expect(response).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(
      `/api/rides/${rideId}/ratings/${raterId}`
    );

    expect(req.request.method).toBe('GET');

    req.flush(mockResponse);
  });

});
