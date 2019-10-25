package com.homeaway.dropwizard.bundle.resilience4j.configuration.retry;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import io.dropwizard.util.Duration;
import io.github.resilience4j.retry.IntervalFunction;

@JsonTypeName("constant")
public class ConstantIntervalFunctionFactory implements IntervalFunctionFactory {

    /**
     * Wait interval. Minimum value is 10 milliseconds. Default is R4j default, currently 500ms.
     */
    @JsonProperty
    private Duration initialInterval = Duration.milliseconds(IntervalFunction.DEFAULT_INITIAL_INTERVAL);

    @Override
    public IntervalFunction build() {
        return IntervalFunction.of(initialInterval.toMilliseconds());
    }

    public Duration getInitialInterval() {
        return initialInterval;
    }

    public void setInitialInterval(Duration initialInterval) {
        this.initialInterval = initialInterval;
    }
}
