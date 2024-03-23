package dev.peter.flightbooking.controller;

import dev.peter.flightbooking.dto.CustomerRequestDto;
import dev.peter.flightbooking.dto.CustomerResponseDto;
import dev.peter.flightbooking.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer")
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
}
