package com.su.springandvue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SpringAndVueApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAndVueApplication.class, args);
    }

}
