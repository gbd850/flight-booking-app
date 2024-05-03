import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { Flight } from '../model/flight';
import { FlightRequest } from '../dto/flightRequest';

@Injectable({
  providedIn: 'root'
})
export class FlightService {

  private apiUrl = "http://localhost:8080/v1/api";

  constructor(private http: HttpClient) { }

  getFlightsByStartLocation(startLocation: string, isFiltered: boolean = false) : Observable<Flight[]> {
    if (startLocation === undefined) {
      return of([]);
    }
    return this.http.get<Flight[]>(`${this.apiUrl}/flights?startLocation=${startLocation}&filterUnavailable=${isFiltered}`);
  }

  getFlightsByEndLocation(endLocation: string, isFiltered: boolean = false) : Observable<Flight[]> {
    if (endLocation === undefined) {
      return of([]);
    }
    return this.http.get<Flight[]>(`${this.apiUrl}/flights?endLocation=${endLocation}&filterUnavailable=${isFiltered}`);
  }

  getFlightsByTimeframe(startDate: Date, endDate: Date, isFiltered: boolean = false) : Observable<Flight[]> {
    if (startDate === undefined || endDate === undefined) {
      return of([]);
    }
    return this.http.get<Flight[]>(`${this.apiUrl}/flights?startDate=${startDate}&endDate=${endDate}&filterUnavailable=${isFiltered}`);
  }
}
