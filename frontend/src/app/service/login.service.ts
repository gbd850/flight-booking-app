import { Injectable } from '@angular/core';
import { CookieService } from 'ngx-cookie-service';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { jwtDecode } from 'jwt-decode';

@Injectable({
  providedIn: 'root'
})
export class LoginService {

  constructor(private cookieService: CookieService) {
    setInterval(() => this.isLoggedIn(), 100000);
  }

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

}
