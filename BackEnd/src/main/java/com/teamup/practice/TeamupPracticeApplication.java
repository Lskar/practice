package com.teamup.practice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.teamup.practice.mapper")
public class TeamupPracticeApplication {

    public static void main(String[] args) {
        SpringApplication.run(TeamupPracticeApplication.class, args);
    }

}
