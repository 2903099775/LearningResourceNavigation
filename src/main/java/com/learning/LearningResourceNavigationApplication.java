package com.learning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableCaching
@MapperScan("com.learning.mapper")
public class LearningResourceNavigationApplication {

    public static void main(String[] args) {
        SpringApplication.run(LearningResourceNavigationApplication.class, args);
    }

}