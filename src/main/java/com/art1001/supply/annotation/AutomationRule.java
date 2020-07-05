package com.art1001.supply.annotation;

import java.lang.annotation.*;

/**
 * @Description
 * @Date:2019/8/19 16:07
 * @Author ddm
 **/
@Documented
//用来修饰注解，是注解的注解，称为元注解
@Retention(RetentionPolicy.RUNTIME)
//在执行对象的类具有给定类型的注释的情况下，将匹配限制为连接点
@Target(ElementType.METHOD)
public @interface AutomationRule {

    /**
     * 任务id
     * @return 任务id
     */
    String value() default "";

    /**
     * 触发条件名称
     * @return 条件名称
     */
    String trigger() default "";

    /**
     * 触发的值
     * @return 传递到接口参数值
     */
    String objectValue() default "";
}
