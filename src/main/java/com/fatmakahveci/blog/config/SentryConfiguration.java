package com.fatmakahveci.blog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import io.sentry.Sentry;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Configuration
public class SentryConfiguration {

    private final String dsn;
    private final String environment;
    private final String release;
    private final double tracesSampleRate;
    private boolean initialized;

    SentryConfiguration(
            @Value("${sentry.dsn:}") String dsn,
            @Value("${sentry.environment:development}") String environment,
            @Value("${sentry.release:${info.app.version:dev}}") String release,
            @Value("${sentry.traces-sample-rate:0.0}") double tracesSampleRate) {
        this.dsn = dsn;
        this.environment = environment;
        this.release = release;
        this.tracesSampleRate = tracesSampleRate;
    }

    @PostConstruct
    void initialize() {
        if (dsn == null || dsn.isBlank()) {
            return;
        }
        Sentry.init(options -> {
            options.setDsn(dsn);
            options.setEnvironment(environment);
            options.setRelease(release);
            options.setSendDefaultPii(false);
            options.setTracesSampleRate(tracesSampleRate);
        });
        initialized = true;
    }

    @PreDestroy
    void close() {
        if (initialized) {
            Sentry.close();
        }
    }
}
