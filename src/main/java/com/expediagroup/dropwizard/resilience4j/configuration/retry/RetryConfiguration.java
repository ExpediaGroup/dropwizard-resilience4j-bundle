package com.expediagroup.dropwizard.resilience4j.configuration.retry;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.github.resilience4j.retry.RetryConfig;

public class RetryConfiguration {

    /**
     * Name for this circuit breaker for use in the registry
     */
    @JsonProperty
    @NotEmpty
    private String name;

    /**
     * Names of exceptions to ignore
     */
    @JsonProperty
    private Class<? extends Throwable>[] ignoreExceptions = null;

    /**
     * Names of exceptions to always retry
     */
    @JsonProperty
    private Class<? extends Throwable>[] retryExceptions = null;

    /**
     * Maximum number of retries
     */
    @JsonProperty
    private int maxAttempts = 3;

    /**
     * Interval function to define wait period between attempts
     */
    @JsonProperty
    @NotNull
    private IntervalFunctionFactory intervalFunction;

    public RetryConfig.Builder toResilience4jConfigBuilder() {
        final RetryConfig.Builder builder = new RetryConfig.Builder()
            .maxAttempts(this.maxAttempts)
            .intervalFunction(intervalFunction.build())
            .ignoreExceptions(ignoreExceptions)
            .retryExceptions(retryExceptions);

        //We are intentionally not supporting the waitDuration() setting, because under the covers it just sets a constant-duration intervalFunction
        //The bundle's approach to that is to use a ConstantIntervalFunctionFactory instead
        //builder.waitDuration(foo);

        return builder;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<? extends Throwable>[] getIgnoreExceptions() {
        return ignoreExceptions;
    }

    public void setIgnoreExceptions(Class<? extends Throwable>[] ignoreExceptions) {
        this.ignoreExceptions = ignoreExceptions;
    }

    public Class<? extends Throwable>[] getRetryExceptions() {
        return retryExceptions;
    }

    public void setRetryExceptions(Class<? extends Throwable>[] retryExceptions) {
        this.retryExceptions = retryExceptions;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public IntervalFunctionFactory getIntervalFunction() {
        return intervalFunction;
    }

    public void setIntervalFunction(IntervalFunctionFactory intervalFunction) {
        this.intervalFunction = intervalFunction;
    }
}
