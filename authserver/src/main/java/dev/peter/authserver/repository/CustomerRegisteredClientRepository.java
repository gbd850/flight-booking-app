package dev.peter.authserver.repository;

import dev.peter.authserver.model.Customer;
import dev.peter.authserver.model.Role;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

import java.util.HashSet;
import java.util.Set;

public class CustomerRegisteredClientRepository implements RegisteredClientRepository {

    private final CustomerRepository customerRepository;


    public CustomerRegisteredClientRepository(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public void save(RegisteredClient registeredClient) {
        Role role = Role.USER;
        if (registeredClient.getScopes().contains("user.write")) {
            role = Role.ADMIN;
        }
        Customer customer = new Customer(null, registeredClient.getClientId(), registeredClient.getClientSecret(), role);

        customerRepository.save(customer);

    }

    @Override
    public RegisteredClient findById(String id) {
        Customer customer = customerRepository.findById(Integer.valueOf(id))
                .orElseThrow(() -> new RuntimeException("Client not found"));

        Set<String> scopes = new HashSet<>();
        scopes.add("user.read");
        if (customer.getRole().equals(Role.ADMIN)) {
            scopes.add("user.write");
        }

        return RegisteredClient
                .withId(customer.getId().toString())
                .clientId(customer.getUsername())
                .clientSecret(customer.getPassword())
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .scopes(strings -> strings.addAll(scopes))
                .build();
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        Customer customer = customerRepository.findByUsername(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        Set<String> scopes = new HashSet<>();
        scopes.add("user.read");
        if (customer.getRole().equals(Role.ADMIN)) {
            scopes.add("user.write");
        }

        return RegisteredClient
                .withId(customer.getId().toString())
                .clientId(customer.getUsername())
                .clientName(customer.getUsername())
                .clientSecret(customer.getPassword())
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .scopes(strings -> strings.addAll(scopes))
                .build();
    }
}
