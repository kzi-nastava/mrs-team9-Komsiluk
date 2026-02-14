import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { MessagePageComponent } from './message-page.component';

describe('MessagePageComponent', () => {
  let component: MessagePageComponent;
  let fixture: ComponentFixture<MessagePageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MessagePageComponent],
      providers: [provideRouter([])]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MessagePageComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
