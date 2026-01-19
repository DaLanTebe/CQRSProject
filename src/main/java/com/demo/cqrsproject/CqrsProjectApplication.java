package com.demo.cqrsproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.demo.cqrsproject.cqrs.query")
public class CqrsProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(CqrsProjectApplication.class, args);
    }

}
