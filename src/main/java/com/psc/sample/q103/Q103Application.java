package com.psc.sample.q103;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableBatchProcessing
public class Q103Application {

    public static void main(String[] args) {
        SpringApplication.run(Q103Application.class, args);
    }

}
