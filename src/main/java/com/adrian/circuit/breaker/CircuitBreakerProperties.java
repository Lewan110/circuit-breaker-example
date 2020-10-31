package com.adrian.circuit.breaker;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "circuit-breaker")
class CircuitBreakerProperties {

    private Integer timeoutDurationInSeconds;
    private Integer failureRateThreshold;
    private Integer waitDurationInOpenStateInSeconds;
    private Integer slidingWindowSize;

}
