package dev.peter.flightbooking.model;

import dev.peter.flightbooking.dto.CustomerRequestDto;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    @Test
    void givenValidCustomerDto_whenUpdateEntityFromDto_thenUpdateEntity() {
        // given
        CustomerRequestDto customerRequestDto = new CustomerRequestDto(null, "newUsername", "newPassword", Role.USER.name());

        Customer customer = new Customer(null, "username", "password", Role.USER, new HashSet<>());
        // when
        customer.updateEntityFromDto(customerRequestDto);
        // then
        assertThat(customer).matches(c ->
                c.getUsername().equals(customerRequestDto.username())
                && c.getPassword().equals(customerRequestDto.password())
                && c.getRole().name().equals(customerRequestDto.role())
                );
    }

    @Test
    void givenValidPartialCustomerDto_whenUpdateEntityFromDto_thenUpdateEntity() {
        // given
        CustomerRequestDto customerRequestDto = new CustomerRequestDto(null, "newUsername", null, Role.USER.name());

        String oldUsername = "username";
        String oldPassword = "password";
        Role oldRole = Role.USER;
        Customer customer = new Customer(null, oldUsername, oldPassword, oldRole, new HashSet<>());
        // when
        customer.updateEntityFromDto(customerRequestDto);
        // then
        assertThat(customer).matches(c ->
                c.getUsername().equals(customerRequestDto.username())
                        && c.getPassword().equals(oldPassword)
                        && c.getRole().name().equals(customerRequestDto.role())
        );
    }

    @Test
    void givenPartialCustomerDtoWithInvalidRole_whenUpdateEntityFromDto_thenUpdateEntityWithOldRole() {
        // given
        CustomerRequestDto customerRequestDto = new CustomerRequestDto(null, "newUsername", null, null);

        String oldUsername = "username";
        String oldPassword = "password";
        Role oldRole = Role.USER;
        Customer customer = new Customer(null, oldUsername, oldPassword, oldRole, new HashSet<>());
        // when
        customer.updateEntityFromDto(customerRequestDto);
        // then
        assertThat(customer).matches(c ->
                c.getUsername().equals(customerRequestDto.username())
                        && c.getPassword().equals(oldPassword)
                        && c.getRole().equals(oldRole)
        );
    }

    @Test
    void givenInvalidCustomerDto_whenUpdateEntityFromDto_thenThrowException() {
        // given
        CustomerRequestDto customerRequestDto = null;

        Customer customer = new Customer(null, "username", "password", Role.USER, new HashSet<>());
        // when
        // then
        assertThatThrownBy(() -> customer.updateEntityFromDto(customerRequestDto))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Invalid customer dto");
    }
}