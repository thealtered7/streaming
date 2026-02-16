package com.keene.streaming;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.keene.streaming.config.DdlGeneratorApplication;

@SpringBootApplication
@ComponentScan(
    basePackages = {"com.keene.streaming", "com.keene.service"},
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = DdlGeneratorApplication.class)
)
@EnableJpaAuditing(dateTimeProviderRef = "dateTimeProvider")
public class StreamingWebApplication {

    private static final Logger logger = LoggerFactory.getLogger(StreamingWebApplication.class);

    @Bean
    public DateTimeProvider dateTimeProvider() {
        return () -> Optional.of(OffsetDateTime.now(ZoneOffset.UTC));
    }

    public static void main(String[] args) {
        logger.info("Starting Streaming Application");
        SpringApplication.run(StreamingWebApplication.class, args);
        logger.info("Streaming Application started successfully");
    }
}
