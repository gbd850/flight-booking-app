import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CookieService } from 'ngx-cookie-service';
import { BehaviorSubject } from 'rxjs';
import { LoginService } from '../service/login.service';


@Component({
  selector: 'app-header',
  standalone: true,
  imports: [RouterModule, CommonModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css',
})
export class HeaderComponent {

  constructor(private cookieService: CookieService, private loginService: LoginService) {
    loginService.isLoggedIn().subscribe((value : boolean) => {
      this.isLoggedIn$.next(value);
      this.user = value ? loginService.getUsername()! : '';
    });
  }

  isLoggedIn$ = new BehaviorSubject(false);

  user: string = '';
}
