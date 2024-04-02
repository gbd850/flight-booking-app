package dev.peter.authserver.config;

import dev.peter.authserver.repository.CustomerRegisteredClientRepository;
import dev.peter.authserver.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

@Configuration
public class ApplicationConfig {

    @Autowired
    private CustomerRepository customerRepository;
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    RegisteredClientRepository inMemoryRegisteredClientRepository() {
        return new CustomerRegisteredClientRepository(customerRepository);
    }
}
