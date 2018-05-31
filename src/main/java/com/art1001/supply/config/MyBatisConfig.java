package com.art1001.supply.config;

import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * Created by 汪亚锋 on 2018/5/3.
 * 扫描mapper.xml文件
 */
@Configuration
@ComponentScan
public class MyBatisConfig {

    @Resource
    private DataSource dataSource;

    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactoryBean sqlSessionFactory(ApplicationContext applicationContext) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        // sessionFactory.setPlugins(new Interceptor[]{new PageInterceptor()});
        sessionFactory.setMapperLocations(applicationContext.getResources("classpath*:mapper/*.xml"));
        return sessionFactory;
    }
}
