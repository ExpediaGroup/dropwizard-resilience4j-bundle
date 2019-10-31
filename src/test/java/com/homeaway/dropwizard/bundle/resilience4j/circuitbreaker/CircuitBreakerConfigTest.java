package com.homeaway.dropwizard.bundle.resilience4j.circuitbreaker;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Rule;
import org.junit.Test;

import com.homeaway.dropwizard.bundle.resilience4j.ResourceTestUtil;
import com.homeaway.dropwizard.bundle.resilience4j.TestApplication;
import com.homeaway.dropwizard.bundle.resilience4j.TestConfiguration;
import com.homeaway.dropwizard.bundle.resilience4j.configuration.CircuitBreakerConfiguration;
import com.homeaway.dropwizard.bundle.resilience4j.configuration.Resilience4jConfiguration;

import io.dropwizard.testing.junit.DropwizardAppRule;
import io.dropwizard.util.Duration;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;

import static org.assertj.core.api.Assertions.assertThat;

public class CircuitBreakerConfigTest {

    //Checking to make sure the app can actually start
    @Rule
    public DropwizardAppRule<TestConfiguration> app =
        new DropwizardAppRule<>(TestApplication.class, ResourceTestUtil.resourceAbsolutePath("/circuitbreaker/config-cb.yaml"));

    @Test
    public void configWithJustCircuitBreaker_runs() throws Exception {
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
    }
}
