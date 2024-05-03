package dev.peter.flightbooking.controller;

import dev.peter.flightbooking.dto.*;
import dev.peter.flightbooking.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("{id}")
    public ResponseEntity<CustomerResponseDto> getCustomerById(@PathVariable Integer id) {
        return new ResponseEntity<>(customerService.getCustomerById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<CustomerResponseDto> createCustomer(@RequestBody CustomerRequestDto customerRequestDto) {
        return new ResponseEntity<>(customerService.createCustomer(customerRequestDto), HttpStatus.CREATED);
    }

    @PatchMapping("{id}")
    public ResponseEntity<CustomerResponseDto> editCustomer(@PathVariable Integer id, @RequestBody CustomerRequestDto customerRequestDto) {
        return new ResponseEntity<>(customerService.editCustomer(id, customerRequestDto), HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Integer id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("role/{username}")
    public ResponseEntity<CustomerRoleResponseDto> getCustomerRole(@PathVariable String username) {
        return new ResponseEntity<>(customerService.getCustomerRole(username), HttpStatus.OK);
    }

    @GetMapping("{id}/bookings")
    public ResponseEntity<CustomerBookingResponseDto> getCustomerBookings(@PathVariable Integer id) {
        return new ResponseEntity<>(customerService.getCustomerBookings(id), HttpStatus.OK);
    }

    @PostMapping("{id}/bookings")
    public ResponseEntity<CustomerBookingResponseDto> bookCustomerFlight(@PathVariable Integer id, @RequestBody CustomerBookingRequestDto bookingRequest) {
        return new ResponseEntity<>(customerService.bookCustomerFlight(id, bookingRequest), HttpStatus.CREATED);
    }

    @DeleteMapping("{id}/bookings/{flightId}")
    public ResponseEntity<Void> deleteBookedCustomerFlight(@PathVariable Integer id, @PathVariable Integer flightId) {
        customerService.deleteBookedCustomerFlight(id, flightId);
        return ResponseEntity.ok().build();
    }
}
