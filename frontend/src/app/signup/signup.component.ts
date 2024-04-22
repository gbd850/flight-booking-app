import { Component } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { CustomerService } from '../service/customer.service';
import { Router } from '@angular/router';
import { BehaviorSubject } from 'rxjs';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './signup.component.html',
  styleUrl: './signup.component.css'
})
export class SignupComponent {

  constructor(
    private formBuilder: FormBuilder,
    private customerService: CustomerService,
    private router: Router
  ) {}

  registerForm = this.formBuilder.group({
    login: '',
    password: '',
  });

  error$ = new BehaviorSubject(false);

  handleErrorResponse() {
    this.error$.next(true);
    this.registerForm.reset();
  }

  handleSuccessfulResponse() {
    setTimeout(() => this.router.navigate(['/']), 1000);
    this.error$.next(false);
    alert("You have successfully created an account!");
  }

  onSubmit(): void {
    const login = this.registerForm.value.login!;
    const password = this.registerForm.value.password!;

    this.customerService.createAccount(login, password).subscribe({
      next: (response) => console.log(response),
      error: () => this.handleErrorResponse(),
      complete: () => this.handleSuccessfulResponse()
    });
  }
}
