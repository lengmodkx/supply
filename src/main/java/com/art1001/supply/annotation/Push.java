package com.art1001.supply.annotation;

import java.lang.annotation.*;

/**
 * @author heshaohua
 * @Description:
 * @date 2018/10/19 14:57
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Push {
    //推送的值
    PushType value() default PushType.Default;
    //推送的名称，任务，日程，文件，分享
    PushName name() default PushName.DEFAULT;
    //推送的类型
    int type() default 0;
}
