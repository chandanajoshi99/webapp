package com.csye6225.Assignment3.config;
import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class MetricsConfig {
    @Value("${env.domain:localhost}")
    String domain;
    @Bean
    public StatsDClient getClient() {
        return new NonBlockingStatsDClient("Metrics", domain, 8125);

    }
}
