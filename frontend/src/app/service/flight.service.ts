import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Flight } from '../model/flight';
import { FlightRequest } from '../dto/flightRequest';

@Injectable({
  providedIn: 'root'
})
export class FlightService {

  private apiUrl = "http://localhost:8080/api";

  private searchData: FlightRequest = {};

  constructor(private http: HttpClient) { }

  getFlightsByStartLocation(startLocation: string, isFiltered: boolean = false) : Observable<Flight[]> {
    return this.http.get<Flight[]>(`${this.apiUrl}/flight?startLocation=${startLocation}&filterUnavailable=${isFiltered}`);
  }

  setSearchData(searchData: FlightRequest) : void {
    this.searchData = searchData;
  }

  getSearchData() : FlightRequest|undefined {
    if (JSON.stringify(this.searchData) === '{}') {
      return undefined;
    }
    return this.searchData;
  }
}
