package com.art1001.supply.config.interceptor;

import com.art1001.supply.shiro.filter.Interceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import javax.annotation.Resource;

/**
 * @author heshaohua
 */
@Configuration
public class WebConfiguration extends WebMvcConfigurationSupport {

    @Value("${excludePathPatterns}")
    private String[] exclude;

    @Resource
    private Interceptor interceptor;

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor).addPathPatterns("/**").excludePathPatterns("/login","/register","/users/**","/captcha","/code","/forget","/organizations/my_org","/news/count","/oss/callback");
        super.addInterceptors(registry);
    }
}
