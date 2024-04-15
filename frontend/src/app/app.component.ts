import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { HttpClientModule, HttpErrorResponse } from '@angular/common/http';
import { Flight } from './model/flight';
import { FlightService } from './service/flight.service';
import { HeaderComponent } from './header/header.component';
import { FooterComponent } from './footer/footer.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, HttpClientModule, HeaderComponent, FooterComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent implements OnInit {
  title = 'frontend';
  flights: Flight[] = [];

  constructor(private flightService: FlightService) {}

  ngOnInit(): void {
    // this.flightService.getFlightsByStartLocation('City1').subscribe({
    //   next: (response: Flight[]) => (this.flights = response),
    //   error: (error: HttpErrorResponse) => alert(error.message),
    // });
  }
}
