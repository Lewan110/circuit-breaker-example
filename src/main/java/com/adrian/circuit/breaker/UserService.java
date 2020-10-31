package com.adrian.circuit.breaker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
public class UserService {

    public UserService(Resilience4JCircuitBreakerFactory circuitBreakerFactory, RestTemplate restTemplate) {
        this.circuitBreakerFactory = circuitBreakerFactory;
        this.restTemplate = restTemplate;
        this.circuitBreaker = circuitBreakerFactory.create("users-circuit-breaker");
    }

    private final Resilience4JCircuitBreakerFactory circuitBreakerFactory;
    private final RestTemplate restTemplate;
    private CircuitBreaker circuitBreaker;

    @Value("${api.users.url}")
    private String usersApiUrl;


    public List<User> getUsers() {
        return circuitBreaker.run(() -> restTemplate.exchange(
                usersApiUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<User>>() {}).getBody(),
                throwable -> getDefaultUserList());
    }

    private List<User> getDefaultUserList() {
        return List.of(new User(1, "example"));
    }

}
