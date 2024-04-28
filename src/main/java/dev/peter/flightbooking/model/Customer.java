package dev.peter.flightbooking.model;

import dev.peter.flightbooking.dto.CustomerRequestDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.Set;

import static java.util.Objects.isNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(value = EnumType.STRING)
    private Role role;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(
            name = "customers_flights",
            joinColumns = @JoinColumn(name = "flight_id"),
            inverseJoinColumns = @JoinColumn(name = "customer_id")
    )
    private Set<Flight> bookedFlights;

    public void updateEntityFromDto(CustomerRequestDto customerRequestDto) {
        if (isNull(customerRequestDto)) {
            throw new NullPointerException("Invalid customer dto");
        }
        this.username = Objects.requireNonNullElse(customerRequestDto.username(), this.username);
        this.password = Objects.requireNonNullElse(customerRequestDto.password(), this.password);
        Role updatedRole;
        try {
            updatedRole = Role.valueOf(customerRequestDto.role());
        } catch (IllegalArgumentException | NullPointerException e) {
            e.printStackTrace();
            updatedRole = this.role;
        }
        this.role = Objects.requireNonNullElse(updatedRole, this.role);
    }

}
