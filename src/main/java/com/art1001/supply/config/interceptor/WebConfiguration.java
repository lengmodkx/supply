package com.art1001.supply.config.interceptor;

import com.art1001.supply.interceptor.RecycleParamCheckInterceptor;
import com.art1001.supply.shiro.filter.Interceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author heshaohua
 */
@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    @Value("${excludePathPatterns}")
    private String[] exclude;

    @Resource
    private Interceptor interceptor;

    @Resource
    private RecycleParamCheckInterceptor recycleParamCheck;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String[] path = new String[]{"/checkcookie",
               "/bind/phone","/bind/wechat","/notbind/wechat",
                "/change_password", "/users/**","/organizations/my_org","/news/count","/oss/callback",
                "/wechatcode","/invite/**","/wechattoken","/files/**/upload","/files/**/model","/files/**/name",
                "/files/deletefile","/logs/exportLogByExcel","/organization/members/expOrgMember","/organization/members/impUser/**",
                "/check_account_exist","/organization/members/addMember1","/druid/**"};
        registry.addInterceptor(interceptor).addPathPatterns("/**").excludePathPatterns(path);
        registry.addInterceptor(recycleParamCheck).addPathPatterns("/recycle_bin/move_task_rb","/recycle_bin/move_file_rb","/recycle_bin/move_share_rb","/recycle_bin/move_schedule_rb","/recycle_bin/move_tag_rb");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
