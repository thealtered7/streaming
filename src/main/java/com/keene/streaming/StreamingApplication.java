package com.keene.streaming;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class StreamingApplication {

    private static final Logger logger = LoggerFactory.getLogger(StreamingApplication.class);

    public static void main(String[] args) {
        logger.info("Starting Streaming Application");
        SpringApplication.run(StreamingApplication.class, args);
        logger.info("Streaming Application started successfully");
    }

    @GetMapping("/health")
    public String health() {
        logger.info("Health check requested");
        return "OK";
    }
} 