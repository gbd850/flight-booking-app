import { CommonModule, formatDate } from '@angular/common';
import { Component } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { FlightService } from '../service/flight.service';
import { Router } from '@angular/router';

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

  currentDate = formatDate(new Date, 'yyyy-MM-dd', 'en');

  searchForm = this.formBuilder.group({
    startDate: this.currentDate,
    endDate: this.currentDate,
    startLocation: '',
    endLocation: '',
    filterUnavailable: false
  });

  onSubmit(): void {
    this.router.navigate(['/search'], {queryParams: this.searchForm.value});
  }
}
