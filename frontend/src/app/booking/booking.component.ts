import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { CustomerService } from '../service/customer.service';
import { CookieService } from 'ngx-cookie-service';
import { Router } from '@angular/router';
import { Flight } from '../model/flight';
import { HttpErrorResponse } from '@angular/common/http';
import { CustomerBookedFlightsResponse } from '../dto/CustomerBookedFlightsResponse';

@Component({
  selector: 'app-booking',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './booking.component.html',
  styleUrl: './booking.component.css'
})
export class BookingComponent implements OnInit {

  constructor(
    private customerService: CustomerService,
    private cookieService: CookieService,
    private router: Router,
  ){}

  customerBookings : Flight[] | undefined = undefined
  failed = false

  ngOnInit(): void {

    if (!this.cookieService.check('token')) {
      this.router.navigate(['/']);
      alert('You are not logged in!');
    }

    this.customerService.getBookings().subscribe({
      next: (response: CustomerBookedFlightsResponse) =>
        (this.customerBookings = response.bookedFlights),
      error: (error: HttpErrorResponse) => (this.failed = true),
    });
  }

  removeBooking(id: number): void {
    this.customerBookings = undefined
    this.customerService.removeBooking(id).subscribe(() => this.ngOnInit());
  }

}
