import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { CustomerService } from '../service/customer.service';
import { CookieService } from 'ngx-cookie-service';
import { Router } from '@angular/router';

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

  ngOnInit(): void {
    if (this.cookieService.check('token')) {
      this.router.navigate(['/']);
      alert('You are already logged in!');
    }
  }

  removeBooking(id: number): void {

    this.customerService.removeBooking(id);
  }

}
