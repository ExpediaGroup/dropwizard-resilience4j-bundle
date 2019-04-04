/*
Copyright 2018 Expedia Group, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.homeaway.dropwizard.bundle.resilience4j.configuration;

import io.dropwizard.util.Duration;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;

/**
 * A configuration for CircuitBreaker.
 */
public class CircuitBreakerConfiguration {

    /**
     * Name for this circuit breaker for use in the registry
     */
    private String name;

    /**
     * Configures the failure rate threshold in percentage above which the CircuitBreaker should trip open and start short-circuiting
     * calls. The threshold must be greater than 0 and not greater than 100. Default value is 50 percentage.
     * <br>
     * See also {@link io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.Builder#failureRateThreshold}.
     */
    private float failureRateThreshold = 50;

    /**
     * Configures the size of the ring buffer when the CircuitBreaker is half open. The CircuitBreaker stores the success/failure
     * success / failure status of the latest calls in a ring buffer. For example, if {@code ringBufferSizeInClosedState} is 10, then
     * at least 10 calls must be evaluated, before the failure rate can be calculated. If only 9 calls have been evaluated the
     * CircuitBreaker will not trip back to closed or open even if all 9 calls have failed. The size must be greater than 10. Default
     * size is 10.
     * <br>
     * See also {@link io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.Builder#ringBufferSizeInHalfOpenState}.
     */
    private int ringBufferSizeInHalfOpenState = 10;

    /**
     * Configures the size of the ring buffer when the CircuitBreaker is closed. The CircuitBreaker stores the success/failure
     * success / failure status of the latest calls in a ring buffer. For example, if {@code ringBufferSizeInClosedState} is 100,
     * then at least 100 calls must be evaluated, before the failure rate can be calculated. If only 99 calls have been evaluated the
     * CircuitBreaker will not trip open even if all 99 calls have failed. The size must be greater than 0. Default size is 100.
     * <br>
     * See also {@link io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.Builder#ringBufferSizeInClosedState}.
     */
    private int ringBufferSizeInClosedState = 100;

    /**
     * Configures the wait duration which specifies how long the CircuitBreaker should stay open, before it switches to half open.
     * Default value is 60000 milliseconds.
     * <br>
     * See also {@link io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.Builder#waitDurationInOpenState}.
     */
    private Duration waitDurationInOpenState = Duration.seconds(60);

    /**
     * Enables automatic transition from OPEN to HALF_OPEN state once the waitDurationInOpenState has passed.
     * Default value is {c}true{/c}
     * See also {@link io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.Builder#enableAutomaticTransitionFromOpenToHalfOpen}.
     */
    private Boolean enableAutomaticTransitionFromOpenToHalfOpen = true;

    public CircuitBreakerConfig.Builder toResilience4jConfigBuilder() {
        CircuitBreakerConfig.Builder builder = CircuitBreakerConfig.custom()
                                          .waitDurationInOpenState(java.time.Duration.ofNanos(this.waitDurationInOpenState.toNanoseconds()))
                                          .ringBufferSizeInClosedState(this.ringBufferSizeInClosedState)
                                          .ringBufferSizeInHalfOpenState(this.ringBufferSizeInHalfOpenState)
                                          .failureRateThreshold(this.failureRateThreshold);

        if (enableAutomaticTransitionFromOpenToHalfOpen) {
            builder.enableAutomaticTransitionFromOpenToHalfOpen();
        }

        return builder;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFailureRateThreshold(float failureRateThreshold) {
        this.failureRateThreshold = failureRateThreshold;
    }

    public float getFailureRateThreshold() {
        return failureRateThreshold;
    }

    public void setRingBufferSizeInHalfOpenState(int ringBufferSizeInHalfOpenState) {
        this.ringBufferSizeInHalfOpenState = ringBufferSizeInHalfOpenState;
    }

    public int getRingBufferSizeInHalfOpenState() {
        return ringBufferSizeInHalfOpenState;
    }

    public int getRingBufferSizeInClosedState() {
        return ringBufferSizeInClosedState;
    }

    public void setRingBufferSizeInClosedState(int ringBufferSizeInClosedState) {
        this.ringBufferSizeInClosedState = ringBufferSizeInClosedState;
    }

    public Duration getWaitDurationInOpenState() {
        return waitDurationInOpenState;
    }

    public void setWaitDurationInOpenState(Duration waitDurationInOpenState) {
        this.waitDurationInOpenState = waitDurationInOpenState;
    }

    public Boolean getEnableAutomaticTransitionFromOpenToHalfOpen() {
        return enableAutomaticTransitionFromOpenToHalfOpen;
    }

    public void setEnableAutomaticTransitionFromOpenToHalfOpen(Boolean enableAutomaticTransitionFromOpenToHalfOpen) {
        this.enableAutomaticTransitionFromOpenToHalfOpen = enableAutomaticTransitionFromOpenToHalfOpen;
    }
}
