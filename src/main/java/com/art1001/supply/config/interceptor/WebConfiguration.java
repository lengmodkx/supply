package com.art1001.supply.config.interceptor;

import com.art1001.supply.interceptor.RecycleParamCheckInterceptor;
import com.art1001.supply.shiro.filter.Interceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import javax.annotation.Resource;
import java.util.List;

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
    private RecycleParamCheckInterceptor recycleParamCheck;

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor).addPathPatterns("/**").excludePathPatterns("/login","/register","/users/**","/captcha","/code","/forget","/organizations/my_org","/news/count","/oss/callback");
        registry.addInterceptor(recycleParamCheck).addPathPatterns("/recycle_bin/move_task_rb","/recycle_bin/move_file_rb","/recycle_bin/move_share_rb","/recycle_bin/move_schedule_rb","/recycle_bin/move_tag_rb");
        super.addInterceptors(registry);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        // 注册Spring data jpa pageable的参数分解器
        argumentResolvers.add(new PageableHandlerMethodArgumentResolver());
    }
}
