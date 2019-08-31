package com.art1001.supply.generator;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

/**
 * @Description
 * @Date:2019/4/29 14:06
 * @Author heshaohua
 **/
public class CodeGenerator {

    public static void main(String[] args) {
        try {
            String[] a = {"prm_order"};
            AutoGenerator mpg = new AutoGenerator();
            // 全局配置
            GlobalConfig gc = new GlobalConfig();
            gc.setOutputDir("D://supply");
            gc.setFileOverride(true);
            gc.setActiveRecord(false);// 不需要ActiveRecord特性的请改为false
            gc.setEnableCache(false);// XML 二级缓存
            gc.setBaseResultMap(true);// XML ResultMap
            gc.setBaseColumnList(false);// XML columList
            gc.setAuthor("heshaohua");// 作者


            // 自定义文件命名，注意 %s 会自动填充表实体属性！
            gc.setControllerName("%sApi");
            gc.setServiceName("%sService");
            gc.setServiceImplName("%sServiceImpl");
            gc.setMapperName("%sMapper");
            gc.setXmlName("%sMapper");
            mpg.setGlobalConfig(gc);


            // 数据源配置
            DataSourceConfig dataSourceConfig = new DataSourceConfig();
            dataSourceConfig.setUrl("jdbc:mysql://localhost/supply01?useSSL=false&serverTimezone=GMT%2B8&characterEncoding=utf8&rewriteBatchedStatements=true");
            dataSourceConfig.setDriverName("com.mysql.cj.jdbc.Driver");
            dataSourceConfig.setUsername("root");
            dataSourceConfig.setPassword("root");


            // 策略配置
            StrategyConfig strategy = new StrategyConfig();
            strategy.setTablePrefix(new String[] { "prm_" });// 此处可以修改为您的表前缀
            strategy.setNaming(NamingStrategy.underline_to_camel);// 表名生成策略
            strategy.setInclude(a); // 需要生成的表


            strategy.setSuperServiceClass(null);
            strategy.setSuperServiceImplClass(null);
            strategy.setSuperMapperClass(null);


            mpg.setStrategy(strategy);
            mpg.setDataSource(dataSourceConfig);


            // 包配置
            PackageConfig pc = new PackageConfig();
            pc.setParent("com.art1001.supply");
            pc.setController("controller");
            pc.setService("service");
            pc.setServiceImpl("serviceImpl");
            pc.setMapper("mapper");
            pc.setEntity("entity");
            pc.setXml("mapper");
            mpg.setPackageInfo(pc);
            // 执行生成
            mpg.execute();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
