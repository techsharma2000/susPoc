package com.sushil.poc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableScheduling
@EnableAsync // Enable async processing for non-blocking MongoDB writes
@EnableJpaRepositories(basePackages = "com.sushil.poc.repository.jpa")
@EnableMongoRepositories(basePackages = "com.sushil.poc.repository.mongo")
public class SushilPocApplication {
    public static void main(String[] args) {
        SpringApplication.run(SushilPocApplication.class, args);
    }
}