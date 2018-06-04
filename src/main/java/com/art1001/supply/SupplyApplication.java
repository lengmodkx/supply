package com.art1001.supply;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages ="com.art1001.supply")
@ServletComponentScan
@SpringBootApplication
public class SupplyApplication {

    public static void main(String[] args) {
        SpringApplication.run(SupplyApplication.class, args);
    }
}
