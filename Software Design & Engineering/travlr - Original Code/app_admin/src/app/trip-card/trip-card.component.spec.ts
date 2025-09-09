import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TripCardComponent } from './trip-card.component';

describe('TripCard', () => {
  let component: TripCardComponent;
  let fixture: ComponentFixture<TripCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TripCardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TripCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
