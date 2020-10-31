package com.adrian.circuit.breaker;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@Configuration
@AllArgsConstructor
public class Management {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public UserService userService(Resilience4JCircuitBreakerFactory circuitBreakerFactory, RestTemplate restTemplate){
        return new UserService(circuitBreakerFactory, restTemplate);
    }

}
