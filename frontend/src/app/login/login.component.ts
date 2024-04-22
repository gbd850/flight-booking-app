import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CustomerService } from '../service/customer.service';
import { HttpErrorResponse } from '@angular/common/http';
import { TokenResponse } from '../dto/TokenResponse';
import { CookieService } from 'ngx-cookie-service';
import { BehaviorSubject, of } from 'rxjs';
import { LoginService } from '../service/login.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent implements OnInit {
  constructor(
    private formBuilder: FormBuilder,
    private customerService: CustomerService,
    private router: Router,
    private cookieService: CookieService,
    private loginService: LoginService
  ) {}

  loginForm = this.formBuilder.group({
    login: '',
    password: '',
  });

  error$ = new BehaviorSubject(false);


  handleErrorResponse(error: HttpErrorResponse) {
    this.error$.next(true);
    this.loginForm.reset();
  }

  handleSuccessfulResponse(token: TokenResponse) {
    this.error$.next(false);
    this.cookieService.set('token', token.access_token, token.expires_in);
    this.cookieService.set('scope', token.scope, token.expires_in);
    this.loginService.isLoggedIn();
    this.router.navigate(['/']);
  }

  ngOnInit(): void {
    if (this.cookieService.check('token')) {
      this.router.navigate(['/']);
      alert('You are already logged in!');
    }
  }

  onSubmit(): void {
    const login = this.loginForm.value.login!;
    const password = this.loginForm.value.password!;

    let role: string;
    this.customerService.getCustomerRole(login).then(
      (result) => {
        const scope = result === 'ADMIN' ? 'user.read user.write' : 'user.read';
        this.customerService.getToken(login, password, scope).subscribe({
          next: (value: TokenResponse) => this.handleSuccessfulResponse(value),
          error: (err: HttpErrorResponse) => this.handleErrorResponse(err),
        });
      },
      (err: HttpErrorResponse) => this.handleErrorResponse(err)
    );
  }
}
