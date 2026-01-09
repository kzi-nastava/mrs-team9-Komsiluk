import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UsageReportsPageComponent } from './usage-reports-page.component';

describe('UsageReportsPageComponent', () => {
  let component: UsageReportsPageComponent;
  let fixture: ComponentFixture<UsageReportsPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UsageReportsPageComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UsageReportsPageComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
