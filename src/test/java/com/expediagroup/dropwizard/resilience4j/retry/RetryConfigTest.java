package com.expediagroup.dropwizard.resilience4j.retry;

import org.assertj.core.api.Assertions;
import org.junit.Rule;
import org.junit.Test;

import com.expediagroup.dropwizard.resilience4j.configuration.Resilience4jConfiguration;
import com.expediagroup.dropwizard.resilience4j.configuration.retry.ConstantIntervalFunctionFactory;
import com.expediagroup.dropwizard.resilience4j.configuration.retry.RetryConfiguration;
import com.expediagroup.dropwizard.resilience4j.ResourceTestUtil;
import com.expediagroup.dropwizard.resilience4j.TestApplication;
import com.expediagroup.dropwizard.resilience4j.TestConfiguration;
import com.expediagroup.dropwizard.resilience4j.configuration.retry.ExponentialBackoffFunctionFactory;
import com.expediagroup.dropwizard.resilience4j.configuration.retry.ExponentialRandomBackoffFunctionFactory;
import com.expediagroup.dropwizard.resilience4j.configuration.retry.RandomizedIntervalFunctionFactory;

import io.dropwizard.testing.junit.DropwizardAppRule;

import static org.assertj.core.api.Assertions.assertThat;

public class RetryConfigTest {

    @Rule
    public DropwizardAppRule<TestConfiguration> app =
        new DropwizardAppRule<>(TestApplication.class, ResourceTestUtil.resourceAbsolutePath("/retry/config-retry.yaml"));

    @Test
    public void configWithJustRetry_runs() throws Exception {
        Resilience4jConfiguration r4jConfig = app.getConfiguration().getResilience4j();

        //Check that retry was configured correctly
        Assertions.assertThat(r4jConfig.getRetryConfigurations()).isNotNull();
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
}
