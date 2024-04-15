import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { FlightService } from '../service/flight.service';
import { Router } from '@angular/router';
import { FlightRequest } from '../dto/flightRequest';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {

  constructor(
    private formBuilder: FormBuilder,
    private fligtService: FlightService,
    private router: Router
  ) {}

  searchForm = this.formBuilder.group({
    startDate: '',
    endDate: '',
    startLocation: '',
    endLocation: ''
  });

  onSubmit(): void {
    this.fligtService.setSearchData(this.searchForm.value as FlightRequest);
    this.router.navigate(['/search']);
  }
}
