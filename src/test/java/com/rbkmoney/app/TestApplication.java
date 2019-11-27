package com.rbkmoney.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan
@SpringBootApplication
public class TestApplication extends SpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

}
