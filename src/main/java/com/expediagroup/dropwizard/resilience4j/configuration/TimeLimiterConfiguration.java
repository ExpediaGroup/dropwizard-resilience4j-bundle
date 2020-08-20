/*
Copyright 2020 Expedia Group, Inc.

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

package com.expediagroup.dropwizard.resilience4j.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.util.Duration;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;

/**
 * A configuration for TimeLimiter.
 */
public class TimeLimiterConfiguration {

    /**
     * Name for this time limiter for use in the registry
     */
    @JsonProperty
    private String name;

    /**
     * Configures the the thread execution timeout.
     * Default value is 1 second.
     */
    @JsonProperty
    private Duration timeoutDuration = Duration.seconds(1);

    /**
     * Whether cancel is called on the running future.
     * Default value is true
     */
    @JsonProperty
    private Boolean cancelRunningFuture = true;

    public TimeLimiterConfig.Builder toResilience4jConfigBuilder() {
        TimeLimiterConfig.Builder builder = new TimeLimiterConfig.Builder()
            .timeoutDuration(java.time.Duration.ofNanos(timeoutDuration.toNanoseconds()))
            .cancelRunningFuture(cancelRunningFuture);

        return builder;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Duration getTimeoutDuration() {
        return timeoutDuration;
    }

    public void setTimeoutDuration(Duration timeoutDuration) {
        this.timeoutDuration = timeoutDuration;
    }

    public Boolean getCancelRunningFuture() {
        return cancelRunningFuture;
    }

    public void setCancelRunningFuture(Boolean cancelRunningFuture) {
        this.cancelRunningFuture = cancelRunningFuture;
    }

}
