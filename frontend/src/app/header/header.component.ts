import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CookieService } from 'ngx-cookie-service';
import { BehaviorSubject } from 'rxjs';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [RouterModule, CommonModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css',
})
export class HeaderComponent implements OnInit {
  constructor(private cookieService: CookieService) {}

  isLoggedIn$ = new BehaviorSubject(false);

  ngOnInit(): void {
    if (this.cookieService.check('token')) {
      this.isLoggedIn$.next(true);
    } else {
      this.isLoggedIn$.next(false);
    }
  }
}
