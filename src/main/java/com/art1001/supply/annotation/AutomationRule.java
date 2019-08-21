package com.art1001.supply.annotation;

import java.lang.annotation.*;

/**
 * @Description
 * @Date:2019/8/19 16:07
 * @Author ddm
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
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
