package com.example.cineflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CineFlowApplication {

    public static void main(String[] args) {
        System.out.println("안녕하세요");
        SpringApplication.run(CineFlowApplication.class, args);
    }

}
