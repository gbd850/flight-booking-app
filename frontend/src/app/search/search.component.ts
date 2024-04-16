import { Component, OnInit } from '@angular/core';
import { FlightService } from '../service/flight.service';
import { CommonModule, JsonPipe } from '@angular/common';
import { Flight } from '../model/flight';
import { HttpErrorResponse, HttpEventType } from '@angular/common/http';
import { FlightRequest } from '../dto/flightRequest';

@Component({
  selector: 'app-search',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './search.component.html',
  styleUrl: './search.component.css',
})
export class SearchComponent implements OnInit {
  constructor(private flightService: FlightService) {}

  searchData: FlightRequest|undefined = undefined;

  flights: Flight[] = [];

  failed = false;

  ngOnInit(): void {
    this.searchData = this.flightService.getSearchData();
    console.log(this.flightService.getSearchData());
    if (this.searchData === undefined) {
      this.failed = true;
      return;
    }
    if (
      this.searchData.startDate === '' ||
      this.searchData.startDate === undefined
    ) {
      if (
        this.searchData.endLocation === '' ||
        this.searchData.endLocation === undefined
      ) {
        this.flightService
          .getFlightsByStartLocation(this.searchData.startLocation!)
          .subscribe({
            next: (response: Flight[]) =>
              (this.flights = [
                ...new Set(response.map((item) => JSON.stringify(item))),
              ].map((item) => JSON.parse(item))),
            error: (error: HttpErrorResponse) => (this.failed = true),
          });
        return;
      }
      this.flightService
        .getFlightsByEndLocation(this.searchData.endLocation!)
        .subscribe({
          next: (response: Flight[]) =>
            (this.flights = [
              ...new Set(
                [
                  ...response.filter(
                    (item) =>
                      item.startLocation === this.searchData?.startLocation
                  ),
                ].map((item) => JSON.stringify(item))
              ),
            ].map((item) => JSON.parse(item))),
          error: (error: HttpErrorResponse) => (this.failed = true),
        });
      return;
    }
    this.flightService
      .getFlightsByTimeframe(
        this.searchData.startDate! as Date,
        this.searchData.endDate! as Date
      )
      .subscribe({
        next: (response: Flight[]) =>
          (this.flights = [
            ...new Set(response.map((item) => JSON.stringify(item))),
          ].map((item) => JSON.parse(item))),
        error: (error: HttpErrorResponse) => (this.failed = true),
      });
    if (
      this.searchData.startLocation !== '' &&
      this.searchData.startLocation !== undefined
    ) {
      this.flightService
        .getFlightsByStartLocation(this.searchData.startLocation!)
        .subscribe({
          next: (response: Flight[]) =>
            (this.flights = [
              ...new Set(
                [
                  ...this.flights,
                  ...response.filter(
                    (item) =>
                      item.startDate === this.searchData?.startDate &&
                      item.endDate === this.searchData.endDate
                  ),
                ].map((item) => JSON.stringify(item))
              ),
            ].map((item) => JSON.parse(item))),
          error: (error: HttpErrorResponse) => (this.failed = true),
        });
    }
    if (
      this.searchData.endLocation !== '' &&
      this.searchData.endLocation !== undefined
    ) {
      this.flightService
        .getFlightsByEndLocation(this.searchData.endLocation!)
        .subscribe({
          next: (response: Flight[]) =>
            (this.flights = [
              ...new Set(
                [
                  ...this.flights,
                  ...response.filter(
                    (item) =>
                      item.startDate === this.searchData?.startDate &&
                      item.endDate === this.searchData.endDate
                  ),
                ].map((item) => JSON.stringify(item))
              ),
            ].map((item) => JSON.parse(item))),
          error: (error: HttpErrorResponse) => (this.failed = true),
        });
    }
  }
}
