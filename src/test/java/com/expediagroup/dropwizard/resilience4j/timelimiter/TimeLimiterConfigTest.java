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

package com.expediagroup.dropwizard.resilience4j.timelimiter;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.expediagroup.dropwizard.resilience4j.ResourceTestUtil;
import com.expediagroup.dropwizard.resilience4j.TestApplication;
import com.expediagroup.dropwizard.resilience4j.TestConfiguration;
import com.expediagroup.dropwizard.resilience4j.configuration.Resilience4jConfiguration;
import com.expediagroup.dropwizard.resilience4j.configuration.TimeLimiterConfiguration;

import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.dropwizard.util.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(DropwizardExtensionsSupport.class)
@DisplayName("TimeLimiterConfig")
class TimeLimiterConfigTest {

    public DropwizardAppExtension<TestConfiguration> app =
        new DropwizardAppExtension<>(TestApplication.class, ResourceTestUtil.resourceAbsolutePath("/timelimiter/config-timelimiter.yaml"));

    @Test
    @DisplayName("time limiter configuration parses successfully")
    void configWithJustTimeLimiter_runs() {
        Resilience4jConfiguration r4jConfig = app.getConfiguration().getResilience4j();

        //Check that time limiter was configured correctly
        Assertions.assertThat(r4jConfig.getTimeLimiterConfigurations()).isNotNull();
        assertThat(r4jConfig.getTimeLimiterConfigurations().size()).isEqualTo(2);

        TimeLimiterConfiguration timeLimiter1 = r4jConfig.getTimeLimiterConfigurations().get(0);
        assertThat(timeLimiter1).isNotNull();
        assertThat(timeLimiter1.getName()).isEqualTo("defaultTimeLimiter");
        assertThat(timeLimiter1.getTimeoutDuration()).isEqualTo(Duration.seconds(1));
        assertThat(timeLimiter1.getCancelRunningFuture()).isEqualTo(true);

        TimeLimiterConfiguration timeLimiter2 = r4jConfig.getTimeLimiterConfigurations().get(1);
        assertThat(timeLimiter2).isNotNull();
        assertThat(timeLimiter2.getName()).isEqualTo("customTimeLimiter");
        assertThat(timeLimiter2.getTimeoutDuration()).isEqualTo(Duration.seconds(5));
        assertThat(timeLimiter2.getCancelRunningFuture()).isEqualTo(false);
    }

}
