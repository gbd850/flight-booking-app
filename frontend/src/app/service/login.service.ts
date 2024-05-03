import { Injectable } from '@angular/core';
import { CookieService } from 'ngx-cookie-service';
import { BehaviorSubject, Observable } from 'rxjs';
import { jwtDecode } from 'jwt-decode';
import { JwtHelperService } from "@auth0/angular-jwt";
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class LoginService {

  constructor(private cookieService: CookieService, private http: HttpClient) {
    setInterval(() => this.isLoggedIn(), 100000);
  }

  jwtHelperService = new JwtHelperService();

  isLoggedIn$ = new BehaviorSubject(false);

  isLoggedIn(): Observable<boolean> {
    if (this.cookieService.check('token')) {
      this.isLoggedIn$.next(true);

    } else {
      this.isLoggedIn$.next(false);
    }
    return this.isLoggedIn$;
  }

  getUsername(): string | undefined {
    return jwtDecode(this.cookieService.get('token')).sub;
  }

  getUserId(): string | undefined {
    return this.jwtHelperService.decodeToken(this.cookieService.get('token')).id;
  }

  getToken(): string | undefined {
    return this.cookieService.get('token');
  }
}
