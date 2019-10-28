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

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.util.Duration;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;

/**
 * A configuration for CircuitBreaker.
 */
public class CircuitBreakerConfiguration {

    /**
     * Name for this circuit breaker for use in the registry
     */
    @JsonProperty
    private String name;

    /**
     * Configures the failure rate threshold in percentage above which the CircuitBreaker should trip open and start short-circuiting
     * calls. The threshold must be greater than 0 and not greater than 100. Default value is 50 percentage.
     * <br>
     * See also {@link io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.Builder#failureRateThreshold}.
     */
    @JsonProperty
    private float failureRateThreshold = 50;

    /**
     * Configures the wait duration which specifies how long the CircuitBreaker should stay open, before it switches to half open.
     * Default value is 60000 milliseconds.
     * <br>
     * See also {@link io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.Builder#waitDurationInOpenState}.
     */
    @JsonProperty
    private Duration waitDurationInOpenState = Duration.seconds(60);

    /**
     * Enables automatic transition from OPEN to HALF_OPEN state once the waitDurationInOpenState has passed.
     * Default value is {c}true{/c}
     * See also {@link io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.Builder#enableAutomaticTransitionFromOpenToHalfOpen}.
     */
    @JsonProperty
    private Boolean enableAutomaticTransitionFromOpenToHalfOpen = true;

    @JsonProperty
    private int minimumNumberOfCalls = CircuitBreakerConfig.DEFAULT_MINIMUM_NUMBER_OF_CALLS;

    @JsonProperty
    private int permittedNumberOfCallsInHalfOpenState = CircuitBreakerConfig.DEFAULT_PERMITTED_CALLS_IN_HALF_OPEN_STATE;

    @JsonProperty
    private int slidingWindowSize = CircuitBreakerConfig.DEFAULT_SLIDING_WINDOW_SIZE;

    @JsonProperty
    private CircuitBreakerConfig.SlidingWindowType slidingWindowType = CircuitBreakerConfig.DEFAULT_SLIDING_WINDOW_TYPE;

    @JsonProperty
    private Duration slowCallDurationThreshold = Duration.seconds(CircuitBreakerConfig.DEFAULT_SLOW_CALL_DURATION_THRESHOLD);

    @JsonProperty
    private float slowCallRateThreshold = CircuitBreakerConfig.DEFAULT_SLOW_CALL_RATE_THRESHOLD;

    @JsonProperty
    private boolean writableStackTraceEnabled = CircuitBreakerConfig.DEFAULT_WRITABLE_STACK_TRACE_ENABLED;

    @JsonProperty
    private Class<? extends Throwable>[] ignoreExceptions = new Class[0];

    @JsonProperty
    private Class<? extends Throwable>[] recordExceptions = new Class[0];

    public CircuitBreakerConfig.Builder toResilience4jConfigBuilder() {
        return CircuitBreakerConfig.custom()
                                   .waitDurationInOpenState(java.time.Duration.ofNanos(this.waitDurationInOpenState.toNanoseconds()))
                                   .automaticTransitionFromOpenToHalfOpenEnabled(enableAutomaticTransitionFromOpenToHalfOpen)
                                   .failureRateThreshold(this.failureRateThreshold)
                                   .minimumNumberOfCalls(minimumNumberOfCalls)
                                   .permittedNumberOfCallsInHalfOpenState(permittedNumberOfCallsInHalfOpenState)
                                   .slidingWindowSize(slidingWindowSize)
                                   .slidingWindowType(slidingWindowType)
                                   .slowCallDurationThreshold(java.time.Duration.ofNanos(slowCallDurationThreshold.toNanoseconds()))
                                   .slowCallRateThreshold(slowCallRateThreshold)
                                   .writableStackTraceEnabled(writableStackTraceEnabled)
                                   .ignoreExceptions(ignoreExceptions)
                                   .recordExceptions(recordExceptions);
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

    public int getMinimumNumberOfCalls() {
        return minimumNumberOfCalls;
    }

    public void setMinimumNumberOfCalls(int minimumNumberOfCalls) {
        this.minimumNumberOfCalls = minimumNumberOfCalls;
    }

    public int getPermittedNumberOfCallsInHalfOpenState() {
        return permittedNumberOfCallsInHalfOpenState;
    }

    public void setPermittedNumberOfCallsInHalfOpenState(int permittedNumberOfCallsInHalfOpenState) {
        this.permittedNumberOfCallsInHalfOpenState = permittedNumberOfCallsInHalfOpenState;
    }

    public int getSlidingWindowSize() {
        return slidingWindowSize;
    }

    public void setSlidingWindowSize(int slidingWindowSize) {
        this.slidingWindowSize = slidingWindowSize;
    }

    public CircuitBreakerConfig.SlidingWindowType getSlidingWindowType() {
        return slidingWindowType;
    }

    public void setSlidingWindowType(CircuitBreakerConfig.SlidingWindowType slidingWindowType) {
        this.slidingWindowType = slidingWindowType;
    }

    public Duration getSlowCallDurationThreshold() {
        return slowCallDurationThreshold;
    }

    public void setSlowCallDurationThreshold(Duration slowCallDurationThreshold) {
        this.slowCallDurationThreshold = slowCallDurationThreshold;
    }

    public float getSlowCallRateThreshold() {
        return slowCallRateThreshold;
    }

    public void setSlowCallRateThreshold(float slowCallRateThreshold) {
        this.slowCallRateThreshold = slowCallRateThreshold;
    }

    public boolean isWritableStackTraceEnabled() {
        return writableStackTraceEnabled;
    }

    public void setWritableStackTraceEnabled(boolean writableStackTraceEnabled) {
        this.writableStackTraceEnabled = writableStackTraceEnabled;
    }

    public Class<? extends Throwable>[] getIgnoreExceptions() {
        return ignoreExceptions;
    }

    public void setIgnoreExceptions(Class<? extends Throwable>[] ignoreExceptions) {
        this.ignoreExceptions = ignoreExceptions;
    }

    public Class<? extends Throwable>[] getRecordExceptions() {
        return recordExceptions;
    }

    public void setRecordExceptions(Class<? extends Throwable>[] recordExceptions) {
        this.recordExceptions = recordExceptions;
    }
}
