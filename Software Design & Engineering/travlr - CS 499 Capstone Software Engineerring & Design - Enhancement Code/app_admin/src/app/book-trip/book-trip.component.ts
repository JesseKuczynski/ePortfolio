/**
 * Renders the booking form.
 * Enforces the required fields with a minimum of at least one(1) traveler.
 * Emits Post through TripDataService on submit.
 */

import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterLink } from '@angular/router';
import { formatDate } from '@angular/common';
import { TripDataService } from '../services/trip-data.service';
import { Trip } from '../models/trip';

function dateNotPastValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const v = control.value;                  
    if (!v) return null;                      
    const picked = new Date(v);
    if (isNaN(picked.getTime())) return { minDate: true };

    const today = new Date();                 
    today.setHours(0,0,0,0);
    picked.setHours(0,0,0,0);

    return picked >= today ? null : { minDate: true };
  };
}

@Component({
  selector: 'app-book-trip',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './book-trip.component.html',
  styleUrls: ['./book-trip.component.css']
})

export class BookTripComponent implements OnInit {
  bookingForm!: FormGroup;
  loading = true;
  submitted = false;

  trip!: Trip;          
  tripCode!: string;

  minDate = new Date().toISOString().slice(0, 10);
  
  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private trips: TripDataService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // build form
    this.bookingForm = this.fb.group({
      fullName: ['', [Validators.required, Validators.maxLength(120)]],
      email: ['', [Validators.required, Validators.email]],
      travelers: [1, [Validators.required, Validators.min(1)]],
      start: ['', [Validators.required, dateNotPastValidator()]]
    });
    
    // read route 
    this.tripCode = this.route.snapshot.paramMap.get('tripCode')!;

    this.trips.getTrip(this.tripCode).subscribe({
      next: (list: Trip[]) => {
        const t = list?.[0];
        if (!t) { this.loading = false; return; }
        this.trip = t;

        const start = t.start
        ? formatDate(t.start as any, 'yyyy-MM-dd', 'en-US')
        : '';

        if (start) this.bookingForm.patchValue({ start });
        this.loading = false;
      },
      error: () => (this.loading = false)
    });
  }

  get f() { return this.bookingForm.controls; }

  get total(): number {
    const travelers = Number(this.bookingForm?.get('travelers')?.value ?? 0);
    const price = Number(this.trip?.perPerson ?? 0);
    return travelers * price;
  }

onSubmit(): void {
  this.submitted = true;
  if (this.bookingForm.invalid) {
    this.bookingForm.markAllAsTouched();
    return;
  }

  this.loading = true;

  this.trips.createBooking(this.tripCode, this.bookingForm.value).subscribe({
    next: (doc: any) => {
      console.log('Saved booking:', doc);
      this.router.navigate(['']); // back to home
    },
    error: (err) => {
      console.error('Booking failed', err);
      alert('Booking failed: ' + (err?.error?.message || err.message || 'Unknown error'));
      this.loading = false;
    }
  });
}



}
