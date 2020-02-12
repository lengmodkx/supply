package com.art1001.supply.config.interceptor;

import com.art1001.supply.shiro.filter.Interceptor;
import com.art1001.supply.shiro.filter.OrgInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
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

    @Resource
    private OrgInterceptor orgInterceptor;

    @Value("${orgPathPatterns}")
    private String[] orgPathPatterns;


    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(orgInterceptor).addPathPatterns("/**");
        registry.addInterceptor(interceptor).addPathPatterns("/**").excludePathPatterns(exclude);
        super.addInterceptors(registry);
    }
}
