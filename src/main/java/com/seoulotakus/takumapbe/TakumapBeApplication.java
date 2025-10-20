package com.seoulotakus.takumapbe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class TakumapBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(TakumapBeApplication.class, args);
    }

}
