import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Customer } from '../model/customer';
import { Role } from '../dto/RoleDto';
import { Observable, lastValueFrom, of } from 'rxjs';
import { TokenResponse } from '../dto/TokenResponse';
import { CustomerResponse } from '../dto/CustomerResponse';
import { CustomerRequest } from '../dto/customerRequest';
import { LoginService } from './login.service';
import { Flight } from '../model/flight';
import { CustomerBookedFlightsResponse } from '../dto/CustomerBookedFlightsResponse';

@Injectable({
  providedIn: 'root',
})
export class CustomerService {
  private apiUrl = 'http://localhost:8080/v1/api';
  private oauthUrl = 'http://localhost:8081/oauth2/token';

  constructor(private http: HttpClient, private loginService: LoginService) {}

async getCustomerRole(login: string) : Promise<string> {

  let error: HttpErrorResponse;

  const source$ = this.http.get<Role>(`${this.apiUrl}/customers/role/${login}`);

  const role = (await lastValueFrom(source$)).role;

  return role;
}

  getToken(login: string, password: string, scope: string) {

    let body = new URLSearchParams();
    body.set('grant_type', 'client_credentials');
    body.set('scope', scope);

    let headers = new HttpHeaders();

    let options = {
      headers: headers
        .set('Content-Type', 'application/x-www-form-urlencoded')
        .set('Authorization', "Basic " + btoa(`${login}:${password}`)),
    };
    return this.http.post<TokenResponse>(this.oauthUrl, body.toString(), options);
  }

  createAccount(login: string, password: string) : Observable<CustomerResponse> {
    const body = { username: login, password: password } as CustomerRequest;
    return this.http.post(`${this.apiUrl}/customers`, body);
  }

  getBookings() : Observable<CustomerBookedFlightsResponse> {
    const userId = this.loginService.getUserId();
    const token = this.loginService.getToken();

    let headers = new HttpHeaders();

    let options = {
      headers: headers
        .set('Authorization', `Bearer ${token}`),
    };

    return this.http.get<CustomerBookedFlightsResponse>(`${this.apiUrl}/customers/${+userId!}/bookings`, options);
  }

  removeBooking(flightId: number) {
    const userId = this.loginService.getUserId();
    const token = this.loginService.getToken();

    let headers = new HttpHeaders();

    let options = {
      headers: headers
        .set('Authorization', `Bearer ${token}`),
    };

    return this.http.delete<void>(`${this.apiUrl}/customers/${+userId!}/bookings/${flightId}`, options);
  }

  addBooking(flightId: number) {
    const userId = this.loginService.getUserId();
    const token = this.loginService.getToken();

    let headers = new HttpHeaders();

    let options = {
      headers: headers
        .set('Content-Type', 'application/json')
        .set('Authorization', `Bearer ${token}`),
    };

    return this.http.post<CustomerBookedFlightsResponse>(`${this.apiUrl}/customers/${+userId!}/bookings`, {flightId: flightId}, options);
  }
}
