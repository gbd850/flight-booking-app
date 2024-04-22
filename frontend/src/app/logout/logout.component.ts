import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CookieService } from 'ngx-cookie-service';
import { BehaviorSubject } from 'rxjs';

@Component({
  selector: 'app-logout',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './logout.component.html',
  styleUrl: './logout.component.css',
})
export class LogoutComponent implements OnInit {
  constructor(private cookieService: CookieService, private router: Router) {}

  isLoggedIn$ = new BehaviorSubject(true);

  ngOnInit(): void {
    setInterval(() => this.router.navigate(['/']), 1000);
    if (!this.cookieService.check('token')) {
      this.isLoggedIn$.next(false);
      alert('You are not logged in!');
    }
    this.cookieService.delete('token');
    this.cookieService.delete('scope');
  }
}
