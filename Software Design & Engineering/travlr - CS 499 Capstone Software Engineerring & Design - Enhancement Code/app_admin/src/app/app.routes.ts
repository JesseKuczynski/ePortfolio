// Routes
// '' = Trip List
// 'book/:tripCode' = Booking form

import { Routes } from '@angular/router';
import { AddTripComponent } from './add-trip/add-trip.component';
import { TripListingComponent } from './trip-listing/trip-listing.component';
import { EditTripComponent } from './edit-trip/edit-trip.component';
import { LoginComponent } from './login/login.component'
import { BookTripComponent } from './book-trip/book-trip.component';

export const routes: Routes = [
    { path: 'add-trip', component: AddTripComponent },
    { path: 'edit-trip', component: EditTripComponent },
    { path: 'login', component: LoginComponent },
    { path: '', component: TripListingComponent, pathMatch: 'full'},
    { path: 'book/:tripCode', component: BookTripComponent }
];
