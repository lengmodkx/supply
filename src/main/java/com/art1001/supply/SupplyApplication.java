package com.art1001.supply;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;
@SpringBootApplication
@MapperScan("com.art1001.supply.mapper")
@EnableScheduling
public class SupplyApplication  extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(SupplyApplication.class, args);
    }

    @Override protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        // 注意这里要指向原先用main方法执行的Application启动类
        return builder.sources(SupplyApplication.class);
    }
}
