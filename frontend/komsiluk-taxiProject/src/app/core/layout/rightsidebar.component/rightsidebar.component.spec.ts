import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { RightsidebarComponent } from './rightsidebar.component';

describe('RightsidebarComponent', () => {
  let component: RightsidebarComponent;
  let fixture: ComponentFixture<RightsidebarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RightsidebarComponent],
      providers: [provideRouter([])]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RightsidebarComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
