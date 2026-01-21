package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
// Ensure we scan all modules
@ComponentScan(basePackages = "com.example.demo")
// Ensure we find entities in other modules
@EntityScan(basePackages = "com.example.demo")
// Ensure we find repositories in other modules
@EnableJpaRepositories(basePackages = "com.example.demo")
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
