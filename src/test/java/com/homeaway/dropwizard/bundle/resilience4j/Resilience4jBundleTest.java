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

package com.homeaway.dropwizard.bundle.resilience4j;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.homeaway.dropwizard.bundle.resilience4j.configuration.CircuitBreakerConfiguration;
import com.homeaway.dropwizard.bundle.resilience4j.configuration.Resilience4jConfiguration;
import com.homeaway.dropwizard.bundle.resilience4j.configuration.retry.ConstantIntervalFunctionFactory;
import com.homeaway.dropwizard.bundle.resilience4j.configuration.retry.ExponentialBackoffFunctionFactory;
import com.homeaway.dropwizard.bundle.resilience4j.configuration.retry.ExponentialRandomBackoffFunctionFactory;
import com.homeaway.dropwizard.bundle.resilience4j.configuration.retry.RandomizedIntervalFunctionFactory;
import com.homeaway.dropwizard.bundle.resilience4j.configuration.retry.RetryConfiguration;

import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import io.dropwizard.util.Duration;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.retry.RetryConfig;

import static org.assertj.core.api.Assertions.assertThat;

public class Resilience4jBundleTest {

    @Rule
    public DropwizardAppRule<TestConfiguration> app =
        new DropwizardAppRule<>(TestApplication.class, ResourceHelpers.resourceFilePath("config.yaml"));

    @Before
    public void setup() {
    }

    @Test
    public void testConfiguration() {
        Resilience4jConfiguration r4jConfig = app.getConfiguration().getResilience4j();
        assertThat(r4jConfig).isNotNull();
        assertThat(r4jConfig.getCircuitBreakers()).isNotNull();
        assertThat(r4jConfig.getCircuitBreakers().size()).isEqualTo(3);

        CircuitBreakerConfiguration breaker1 = r4jConfig.getCircuitBreakers().get(0);
        assertThat(breaker1).isNotNull();
        assertThat(breaker1.getName()).isEqualTo("testBreaker1");
        assertThat(breaker1.getEnableAutomaticTransitionFromOpenToHalfOpen()).isFalse();
        assertThat(breaker1.getWaitDurationInOpenState()).isEqualTo(Duration.seconds(30));
        assertThat(breaker1.getFailureRateThreshold()).isEqualTo(10);
        assertThat(breaker1.getMinimumNumberOfCalls()).isEqualTo(5);
        assertThat(breaker1.getPermittedNumberOfCallsInHalfOpenState()).isEqualTo(2);
        assertThat(breaker1.getSlidingWindowSize()).isEqualTo(4);
        assertThat(breaker1.getSlidingWindowType()).isEqualTo(CircuitBreakerConfig.SlidingWindowType.TIME_BASED);
        assertThat(breaker1.getSlowCallRateThreshold()).isEqualTo(1.5f);
        assertThat(breaker1.getSlowCallDurationThreshold()).isEqualTo(Duration.milliseconds(10));
        assertThat(breaker1.isWritableStackTraceEnabled()).isEqualTo(true);
        assertThat(breaker1.getIgnoreExceptions()).containsExactly(NullPointerException.class);
        assertThat(breaker1.getRecordExceptions()).containsExactly(IllegalArgumentException.class);

        CircuitBreakerConfiguration breaker2 = r4jConfig.getCircuitBreakers().get(1);
        assertThat(breaker2).isNotNull();
        assertThat(breaker2.getName()).isEqualTo("testBreaker2");
        assertThat(breaker2.getEnableAutomaticTransitionFromOpenToHalfOpen()).isTrue();
        assertThat(breaker2.getWaitDurationInOpenState()).isEqualTo(Duration.seconds(CircuitBreakerConfig.DEFAULT_WAIT_DURATION_IN_OPEN_STATE));
        assertThat(breaker2.getFailureRateThreshold()).isEqualTo(CircuitBreakerConfig.DEFAULT_FAILURE_RATE_THRESHOLD);
        assertThat(breaker2.getMinimumNumberOfCalls()).isEqualTo(CircuitBreakerConfig.DEFAULT_MINIMUM_NUMBER_OF_CALLS);
        assertThat(breaker2.getPermittedNumberOfCallsInHalfOpenState()).isEqualTo(CircuitBreakerConfig.DEFAULT_PERMITTED_CALLS_IN_HALF_OPEN_STATE);
        assertThat(breaker2.getSlidingWindowSize()).isEqualTo(CircuitBreakerConfig.DEFAULT_SLIDING_WINDOW_SIZE);
        assertThat(breaker2.getSlidingWindowType()).isEqualTo(CircuitBreakerConfig.DEFAULT_SLIDING_WINDOW_TYPE);
        assertThat(breaker2.getSlowCallRateThreshold()).isEqualTo(CircuitBreakerConfig.DEFAULT_SLOW_CALL_RATE_THRESHOLD);
        assertThat(breaker2.getSlowCallDurationThreshold()).isEqualTo(Duration.seconds(CircuitBreakerConfig.DEFAULT_SLOW_CALL_DURATION_THRESHOLD));
        assertThat(breaker2.isWritableStackTraceEnabled()).isEqualTo(true);
        assertThat(breaker2.getIgnoreExceptions()).isNullOrEmpty();
        assertThat(breaker2.getRecordExceptions()).isNullOrEmpty();

        CircuitBreakerConfiguration breaker3 = r4jConfig.getCircuitBreakers().get(2);
        assertThat(breaker3).isNotNull();
        assertThat(breaker3.getName()).isEqualTo("testBreaker3");
        assertThat(breaker3.getSlidingWindowSize()).isEqualTo(10);
        assertThat(breaker3.getSlidingWindowType()).isEqualTo(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED);
        assertThat(breaker2.getIgnoreExceptions()).isNullOrEmpty();
        assertThat(breaker2.getRecordExceptions()).isNullOrEmpty();

        //Check that the configurator function was called for each breaker config
        List<Pair<String, CircuitBreakerConfig.Builder>> breakersSeenInConfiguration =
            ((TestApplication) app.getApplication()).getBreakersSeenInConfiguration();
        assertThat(breakersSeenInConfiguration.size()).isEqualTo(3);
        assertThat(breakersSeenInConfiguration.get(0).getKey()).isEqualTo("testBreaker1");
        assertThat(breakersSeenInConfiguration.get(1).getKey()).isEqualTo("testBreaker2");
        assertThat(breakersSeenInConfiguration.get(2).getKey()).isEqualTo("testBreaker3");

        assertThat(r4jConfig.getCircuitBreakerRegistry().circuitBreaker("testBreaker1")).isNotNull();
        assertThat(r4jConfig.getCircuitBreakerRegistry().circuitBreaker("testBreaker2")).isNotNull();
        assertThat(r4jConfig.getCircuitBreakerRegistry().circuitBreaker("testBreaker3")).isNotNull();

        //Check that retry was configured correctly
        assertThat(r4jConfig.getRetryConfigurations()).isNotNull();
        assertThat(r4jConfig.getRetryConfigurations().size()).isEqualTo(4);

        RetryConfiguration retry1 = r4jConfig.getRetryConfigurations().get(0);
        assertThat(retry1).isNotNull();
        assertThat(retry1.getName()).isEqualTo("constantRetry");
        assertThat(retry1.getMaxAttempts()).isEqualTo(1);
        assertThat(retry1.getIgnoreExceptions()).containsExactly(RuntimeException.class);
        assertThat(retry1.getRetryExceptions()).containsExactly(IllegalArgumentException.class);
        assertThat(retry1.getIntervalFunction().getClass()).isEqualTo(ConstantIntervalFunctionFactory.class);

        RetryConfiguration retry2 = r4jConfig.getRetryConfigurations().get(1);
        assertThat(retry2).isNotNull();
        assertThat(retry2.getName()).isEqualTo("randomizedRetry");
        assertThat(retry2.getMaxAttempts()).isEqualTo(2);
        assertThat(retry2.getIgnoreExceptions()).isNullOrEmpty();
        assertThat(retry2.getRetryExceptions()).isNullOrEmpty();
        assertThat(retry2.getIntervalFunction().getClass()).isEqualTo(RandomizedIntervalFunctionFactory.class);

        RetryConfiguration retry3 = r4jConfig.getRetryConfigurations().get(2);
        assertThat(retry3).isNotNull();
        assertThat(retry3.getName()).isEqualTo("exponentialBackoffRetry");
        assertThat(retry3.getMaxAttempts()).isEqualTo(3);
        assertThat(retry3.getIgnoreExceptions()).isNullOrEmpty();
        assertThat(retry3.getRetryExceptions()).isNullOrEmpty();
        assertThat(retry3.getIntervalFunction().getClass()).isEqualTo(ExponentialBackoffFunctionFactory.class);

        RetryConfiguration retry4 = r4jConfig.getRetryConfigurations().get(3);
        assertThat(retry4).isNotNull();
        assertThat(retry4.getName()).isEqualTo("exponentialRandomizedBackoffRetry");
        assertThat(retry4.getMaxAttempts()).isEqualTo(4);
        assertThat(retry4.getIgnoreExceptions()).isNullOrEmpty();
        assertThat(retry4.getRetryExceptions()).isNullOrEmpty();
        assertThat(retry4.getIntervalFunction().getClass()).isEqualTo(ExponentialRandomBackoffFunctionFactory.class);
    }

    public static class TestConfiguration extends Configuration {

        private Resilience4jConfiguration resilience4j;

        public Resilience4jConfiguration getResilience4j() {
            return resilience4j;
        }
    }

    public static class TestApplication extends Application<TestConfiguration> {

        private List<Pair<String, CircuitBreakerConfig.Builder>> breakersSeenInConfiguration = new ArrayList<>();

        private List<Pair<String, RetryConfig.Builder>> retryersSeenInConfiguration = new ArrayList<>();

        @Override
        public void initialize(Bootstrap<TestConfiguration> bootstrap) {
            Resilience4jBundle<TestConfiguration> bundle =
                new Resilience4jBundle<>(TestConfiguration::getResilience4j,
                                         this::addBreakerConfigurationToTestList,
                                         this::addRetryConfigurationToTestList);

            bootstrap.addBundle(bundle);
        }

        @Override
        public void run(TestConfiguration configuration, Environment environment) {
        }

        private void addBreakerConfigurationToTestList(String key, CircuitBreakerConfig.Builder builder) {
            breakersSeenInConfiguration.add(Pair.of(key, builder));
        }

        private void addRetryConfigurationToTestList(String key, RetryConfig.Builder builder) {
            retryersSeenInConfiguration.add(Pair.of(key, builder));
        }

        public List<Pair<String, CircuitBreakerConfig.Builder>> getBreakersSeenInConfiguration() {
            return breakersSeenInConfiguration;
        }

        public List<Pair<String, RetryConfig.Builder>> getRetryersSeenInConfiguration() {
            return retryersSeenInConfiguration;
        }
    }
}
