package dev.peter.flightbooking.controller;

import dev.peter.flightbooking.dto.CustomerRequestDto;
import dev.peter.flightbooking.model.Customer;
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
    public ResponseEntity<Customer> getCustomerById(@PathVariable Integer id) {
        return new ResponseEntity<>(customerService.getCustomerById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Customer> createCustomer(@RequestBody CustomerRequestDto customerRequestDto) {
        return new ResponseEntity<>(customerService.createCustomer(customerRequestDto), HttpStatus.CREATED);
    }

    @PatchMapping("{id}")
    public ResponseEntity<Customer> editCustomer(@PathVariable Integer id, @RequestBody CustomerRequestDto customerRequestDto) {
        return new ResponseEntity<>(customerService.editCustomer(id, customerRequestDto), HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Integer id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok().build();
    }
}
