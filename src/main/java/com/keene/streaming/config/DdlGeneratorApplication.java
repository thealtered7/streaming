package com.keene.streaming.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * Minimal Spring Boot application to generate DDL scripts.
 * This application initializes JPA metadata and generates the schema,
 * then exits without starting a web server.
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.keene.streaming"})
public class DdlGeneratorApplication {
    
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(DdlGeneratorApplication.class);
        // Disable web server
        app.setWebApplicationType(org.springframework.boot.WebApplicationType.NONE);
        app.run(args);
    }
    
    @Bean
    public CommandLineRunner ddlGenerator() {
        return args -> {
            System.out.println("DDL generation complete. Check build/ddl/schema.sql");
            System.exit(0);
        };
    }
}

