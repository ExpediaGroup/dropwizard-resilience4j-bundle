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
import com.homeaway.dropwizard.bundle.resilience4j.configuration.CircuitBreakerConfiguration;
import com.homeaway.dropwizard.bundle.resilience4j.configuration.Resilience4jConfiguration;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import io.dropwizard.util.Duration;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;

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
        assertThat(breaker1.getFailureRateThreshold()).isEqualTo(10);
        assertThat(breaker1.getRingBufferSizeInClosedState()).isEqualTo(50);
        assertThat(breaker1.getRingBufferSizeInHalfOpenState()).isEqualTo(5);
        assertThat(breaker1.getWaitDurationInOpenState()).isEqualTo(Duration.seconds(30));

        assertThat(r4jConfig.getCircuitBreakers().get(1).getName()).isEqualTo("testBreaker2");
        assertThat(r4jConfig.getCircuitBreakers().get(2).getName()).isEqualTo("testBreaker3");

        //Check that the configurator function was called for each breaker config
        List<Pair<String, CircuitBreakerConfig.Builder>> breakersSeenInConfiguration = ((TestApplication)app.getApplication()).getBreakersSeenInConfiguration();
        assertThat(breakersSeenInConfiguration.size()).isEqualTo(3);
        assertThat(breakersSeenInConfiguration.get(0).getKey()).isEqualTo("testBreaker1");
        assertThat(breakersSeenInConfiguration.get(1).getKey()).isEqualTo("testBreaker2");
        assertThat(breakersSeenInConfiguration.get(2).getKey()).isEqualTo("testBreaker3");

        assertThat(r4jConfig.getCircuitBreakerRegistry().circuitBreaker("testBreaker1")).isNotNull();
        assertThat(r4jConfig.getCircuitBreakerRegistry().circuitBreaker("testBreaker2")).isNotNull();
        assertThat(r4jConfig.getCircuitBreakerRegistry().circuitBreaker("testBreaker3")).isNotNull();
    }

    public static class TestConfiguration extends Configuration {

        private Resilience4jConfiguration resilience4j;

        public Resilience4jConfiguration getResilience4j() {
            return resilience4j;
        }
    }

    public static class TestApplication extends Application<TestConfiguration> {

        private List<Pair<String, CircuitBreakerConfig.Builder>> breakersSeenInConfiguration = new ArrayList<>();

        @Override
        public void initialize(Bootstrap<TestConfiguration> bootstrap) {
            Resilience4jBundle<TestConfiguration> bundle = new Resilience4jBundle<>(TestConfiguration::getResilience4j, this::addBreakerConfigurationToTestList);

            bootstrap.addBundle(bundle);
        }

        @Override
        public void run(TestConfiguration configuration, Environment environment) {
        }

        private void addBreakerConfigurationToTestList(String key, CircuitBreakerConfig.Builder builder) {
            breakersSeenInConfiguration.add(Pair.of(key, builder));
        }

        public List<Pair<String, CircuitBreakerConfig.Builder>> getBreakersSeenInConfiguration() {
            return breakersSeenInConfiguration;
        }
    }
}
