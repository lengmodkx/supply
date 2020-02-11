package com.art1001.supply.config.interceptor;

import com.art1001.supply.shiro.filter.Interceptor;
import com.art1001.supply.shiro.filter.OrgInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * @author heshaohua
 */
@Configuration
public class WebConfiguration extends WebMvcConfigurationSupport {

    @Value("${excludePathPatterns}")
    private String[] exclude;

    @Bean
    Interceptor localInterceptor() {
        return new Interceptor();
    }

    @Value("${orgPathPatterns}")
    private String[] orgPathPatterns;


    @Bean
    OrgInterceptor localOrgInterceptor() {
        return new OrgInterceptor();
    }

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localOrgInterceptor()).addPathPatterns(orgPathPatterns);
        registry.addInterceptor(localInterceptor()).addPathPatterns("/**").excludePathPatterns(exclude);
        super.addInterceptors(registry);
    }
}
