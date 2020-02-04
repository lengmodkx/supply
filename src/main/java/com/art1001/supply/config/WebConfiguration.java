package com.art1001.supply.config;

import com.art1001.supply.shiro.filter.Interceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
public class WebConfiguration extends WebMvcConfigurationSupport {

    @Value("${excludePathPatterns}")
    private String[] exclude;

    @Bean
    Interceptor localInterceptor() {
        return new Interceptor();
    }

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localInterceptor()).addPathPatterns("/**").excludePathPatterns(exclude);
        super.addInterceptors(registry);
    }
}
