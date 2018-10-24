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
    PushType value() default PushType.Default;
    int type() default 0;
}
