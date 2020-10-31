package com.adrian.circuit.breaker;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@AllArgsConstructor
@Slf4j
public class UserService {

    private final CircuitBreakerFactory circuitBreakerFactory;
    private final RestTemplate restTemplate;

    public List<User> getUsers() {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("circuitbreaker");
        String url = "https://jsonplaceholder.typicode.com/users";

        return circuitBreaker.run(() -> restTemplate.exchange("https://bitpay.com/api/rates",
                HttpMethod.GET, null, new ParameterizedTypeReference<List<User>>() {
                }).getBody(), throwable -> getDefaultUserList());
    }

    private List<User> getDefaultUserList() {
        return List.of(new User(1, "example"));
    }

}
