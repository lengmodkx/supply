package com.art1001.supply.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * Created by 汪亚锋 on 2018/5/3.
 */
//@Configuration
//@ComponentScan
public class TransactionConfig  {

//    @Resource
//    private DataSource dataSource;
//
//    @Bean(name = "transactionManager")
//    @Override
//    public PlatformTransactionManager annotationDrivenTransactionManager() {
//        return new DataSourceTransactionManager(dataSource);
//    }

}
