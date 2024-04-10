import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Flight } from '../model/flight';

@Injectable({
  providedIn: 'root'
})
export class FlightService {

  private apiUrl = "http://localhost:8080/api";

  constructor(private http: HttpClient) { }

  getFlightsByStartLocation(startLocation: string, isFiltered: boolean = false) : Observable<Flight[]> {
    return this.http.get<Flight[]>(`${this.apiUrl}/flight?startLocation=${startLocation}&filterUnavailable=${isFiltered}`);
  }

}
