package com.crud_base;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
public class CrudBaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrudBaseApplication.class, args);
    }

}
