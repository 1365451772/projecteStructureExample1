package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@SpringBootApplication
@EnableJpaRepositories(basePackages = {"com.example.demo"})
@EnableJpaAuditing
@EnableScheduling
@EnableSwagger2

public class StartUpApplication {

    public static void main(String[] args) {
        SpringApplication.run(StartUpApplication.class, args);
    }
}