import { Component, OnInit } from '@angular/core';
import { FlightService } from '../service/flight.service';
import { CommonModule } from '@angular/common';
import { Flight } from '../model/flight';
import { HttpErrorResponse, HttpEventType } from '@angular/common/http';

@Component({
  selector: 'app-search',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './search.component.html',
  styleUrl: './search.component.css',
})
export class SearchComponent implements OnInit {
  constructor(private flightService: FlightService) {}

  searchData = this.flightService.getSearchData();

  flights : Flight[] = [];

  failed = false;

  ngOnInit(): void {
    if (this.searchData === undefined) {
      this.failed = true;
      return;
    }
    if (this.searchData.startDate === '' || this.searchData.startDate === undefined) {
      this.flightService.getFlightsByStartLocation(this.searchData.startLocation!).subscribe( {
        next: (response: Flight[]) => (this.flights = response),
        error: (error: HttpErrorResponse) => this.failed = true,
      });
    }
  }
}
